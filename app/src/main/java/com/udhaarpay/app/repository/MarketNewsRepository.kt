package com.udhaarpay.app.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.Html
import com.udhaarpay.app.data.model.MarketNewsItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketNewsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    suspend fun fetchLatestMarketNews(): Result<List<MarketNewsItem>> = withContext(Dispatchers.IO) {
        if (!isInternetAvailable()) {
            return@withContext Result.failure(IllegalStateException("No internet connection"))
        }

        return@withContext try {
            val feeds = listOf(
                "https://economictimes.indiatimes.com/markets/rssfeeds/1977021501.cms",
                "https://www.livemint.com/rss/markets",
                "https://news.google.com/rss/search?q=stock+market+india+when:1d&hl=en-IN&gl=IN&ceid=IN:en",
                "https://news.google.com/rss/search?q=nifty+sensex+when:1d&hl=en-IN&gl=IN&ceid=IN:en",
                "https://news.google.com/rss/search?q=site:moneycontrol.com+market+when:1d&hl=en-IN&gl=IN&ceid=IN:en"
            )

            val combined = feeds.flatMap { url ->
                val xml = fetchFeed(url) ?: return@flatMap emptyList()
                parseRss(xml)
            }
                .distinctBy { "${it.title}|${it.link}" }
                .sortedByDescending { it.publishedAtMillis }
                .take(80)

            val enriched = enrichNewsWithImages(combined.take(28)) + combined.drop(28)
            val cleaned = enriched
                .map { item ->
                    item.copy(
                        title = item.title.trim(),
                        source = item.source.ifBlank { extractSourceFromUrl(item.link) }
                    )
                }
                .sortedByDescending { it.publishedAtMillis }
                .take(60)

            Result.success(cleaned)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    private suspend fun enrichNewsWithImages(items: List<MarketNewsItem>): List<MarketNewsItem> = coroutineScope {
        items.map { item ->
            async {
                if (!item.imageUrl.isNullOrBlank()) {
                    item
                } else {
                    val finalLink = resolveFinalLink(item.link)
                    val html = fetchHtml(finalLink)
                    val ogImage = html?.let { extractMetaImage(it) }
                    item.copy(
                        link = finalLink,
                        imageUrl = ogImage
                    )
                }
            }
        }.awaitAll()
    }

    private fun fetchFeed(url: String): String? {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            return response.body?.string()
        }
    }

    private fun fetchHtml(url: String): String? {
        return try {
            val request = Request.Builder().url(url).get().build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                response.body?.string()
            }
        } catch (_: Throwable) {
            null
        }
    }

    private fun resolveFinalLink(url: String): String {
        return try {
            val request = Request.Builder().url(url).get().build()
            client.newCall(request).execute().use { response ->
                response.request.url.toString()
            }
        } catch (_: Throwable) {
            url
        }
    }

    private fun parseRss(xml: String): List<MarketNewsItem> {
        val parserFactory = XmlPullParserFactory.newInstance()
        val parser = parserFactory.newPullParser().apply {
            setInput(StringReader(xml))
        }

        val items = mutableListOf<MarketNewsItem>()
        var eventType = parser.eventType
        var currentTag: String? = null

        var title = ""
        var link = ""
        var pubDate = ""
        var source = ""
        var description = ""
        var imageUrl: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = parser.name
                    val tag = parser.name.lowercase(Locale.getDefault())

                    if (tag == "item") {
                        title = ""
                        link = ""
                        pubDate = ""
                        source = ""
                        description = ""
                        imageUrl = null
                    }

                    if (tag.contains("media:content") || tag.contains("media:thumbnail") || tag == "enclosure") {
                        val maybeUrl = parser.getAttributeValue(null, "url")
                        if (!maybeUrl.isNullOrBlank()) {
                            imageUrl = maybeUrl
                        }
                    }
                }

                XmlPullParser.TEXT -> {
                    val value = parser.text.orEmpty().trim()
                    when (currentTag?.lowercase(Locale.getDefault())) {
                        "title" -> if (title.isBlank()) title = decodeHtml(value)
                        "link" -> if (link.isBlank()) link = value
                        "pubdate" -> if (pubDate.isBlank()) pubDate = value
                        "description" -> if (description.isBlank()) description = decodeHtml(value)
                        "source" -> if (source.isBlank()) source = decodeHtml(value)
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name.equals("item", true)) {
                        if (title.isNotBlank() && link.isNotBlank()) {
                            val extractedImage = imageUrl ?: extractImageFromDescription(description)
                            items.add(
                                MarketNewsItem(
                                    title = title,
                                    source = source.ifBlank { extractSourceFromUrl(link) },
                                    link = link,
                                    publishedAtMillis = parsePublishedAt(pubDate),
                                    imageUrl = extractedImage,
                                    summary = cleanDescription(description)
                                )
                            )
                        }
                    }
                    if (parser.name.equals(currentTag, true)) currentTag = null
                }
            }
            eventType = parser.next()
        }

        return items
    }

    private fun extractImageFromDescription(description: String?): String? {
        if (description.isNullOrBlank()) return null
        val regex = Regex("""<img[^>]*src=['"]([^'"]+)['"][^>]*>""", RegexOption.IGNORE_CASE)
        return regex.find(description)?.groupValues?.getOrNull(1)
    }

    private fun extractMetaImage(html: String): String? {
        val patterns = listOf(
            Regex("""<meta[^>]+property=['"]og:image['"][^>]+content=['"]([^'"]+)['"]""", RegexOption.IGNORE_CASE),
            Regex("""<meta[^>]+content=['"]([^'"]+)['"][^>]+property=['"]og:image['"]""", RegexOption.IGNORE_CASE),
            Regex("""<meta[^>]+name=['"]twitter:image['"][^>]+content=['"]([^'"]+)['"]""", RegexOption.IGNORE_CASE),
            Regex("""<meta[^>]+content=['"]([^'"]+)['"][^>]+name=['"]twitter:image['"]""", RegexOption.IGNORE_CASE)
        )
        for (pattern in patterns) {
            val value = pattern.find(html)?.groupValues?.getOrNull(1)
            if (!value.isNullOrBlank()) return value
        }
        return null
    }

    private fun cleanDescription(description: String?): String? {
        if (description.isNullOrBlank()) return null
        return description
            .replace(Regex("<[^>]*>"), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
            .take(180)
    }

    private fun decodeHtml(text: String): String {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    private fun parsePublishedAt(raw: String): Long {
        if (raw.isBlank()) return System.currentTimeMillis()
        val normalized = raw.replace("<![CDATA[", "").replace("]]>", "").trim()
        val formats = listOf(
            "EEE, dd MMM yyyy HH:mm:ss zzz",
            "EEE, dd MMM yyyy HH:mm zzz",
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "EEE, dd MMM yyyy HH:mm Z"
        )
        for (pattern in formats) {
            try {
                val formatter = SimpleDateFormat(pattern, Locale.ENGLISH).apply {
                    timeZone = TimeZone.getTimeZone("GMT")
                }
                val date = formatter.parse(normalized)
                if (date != null) return date.time
            } catch (_: Throwable) {
                // Try next format.
            }
        }
        return System.currentTimeMillis()
    }

    private fun extractSourceFromUrl(url: String): String {
        return runCatching {
            val host = URI(url).host ?: return@runCatching "Market News"
            host
                .removePrefix("www.")
                .substringBefore(".")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }.getOrElse { "Market News" }
    }

    private fun isInternetAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
