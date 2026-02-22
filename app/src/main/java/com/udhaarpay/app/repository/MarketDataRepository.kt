package com.udhaarpay.app.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.JsonParser
import com.udhaarpay.app.data.IndianMarketUniverse
import com.udhaarpay.app.data.model.MarketQuote
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue
import kotlin.random.Random

@Singleton
class MarketDataRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client = OkHttpClient.Builder().build()

    private val fixedSeedPrices = mapOf(
        "RELIANCE" to 2920.0,
        "TCS" to 4230.0,
        "HDFCBANK" to 1715.0,
        "INFY" to 1880.0,
        "ICICIBANK" to 1210.0,
        "SBIN" to 775.0,
        "ADANIENT" to 3170.0,
        "TATAMOTORS" to 1060.0,
        "ITC" to 437.0,
        "AXISBANK" to 1140.0
    )
    private val basePrices = mutableMapOf<String, Double>().apply {
        putAll(fixedSeedPrices)
        IndianMarketUniverse.symbols.forEach { symbol ->
            putIfAbsent(symbol, 200.0 + (symbol.hashCode().absoluteValue % 3600))
        }
    }

    private val sparkHistory = mutableMapOf<String, MutableList<Float>>()

    fun observeQuotes(symbols: List<String>): Flow<List<MarketQuote>> = flow {
        var previous = symbols.associateWith { symbol -> basePrices[symbol] ?: 100.0 }

        while (currentCoroutineContext().isActive) {
            val live = fetchYahooQuotes(symbols)
            val merged = mutableListOf<MarketQuote>()

            symbols.forEach { symbol ->
                val quote = live[symbol]
                if (quote != null) {
                    previous = previous.toMutableMap().apply { put(symbol, quote.currentPrice) }
                    merged.add(updateSparkline(quote))
                } else {
                    val simulated = simulateQuote(symbol, previous[symbol] ?: basePrices[symbol] ?: 100.0)
                    previous = previous.toMutableMap().apply { put(symbol, simulated.currentPrice) }
                    merged.add(updateSparkline(simulated))
                }
            }

            emit(merged)
            delay(2000L)
        }
    }.flowOn(Dispatchers.IO)

    private fun fetchYahooQuotes(symbols: List<String>): Map<String, MarketQuote> {
        if (!isInternetAvailable()) return emptyMap()
        return try {
            val yahooSymbols = symbols.joinToString(",") { "${it.uppercase(Locale.getDefault())}.NS" }
            val url = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=$yahooSymbols"
            val request = Request.Builder().url(url).get().build()
            val body = client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyMap()
                response.body?.string()
            } ?: return emptyMap()

            parseYahooResponse(body)
        } catch (_: Throwable) {
            emptyMap()
        }
    }

    private fun parseYahooResponse(json: String): Map<String, MarketQuote> {
        val root = JsonParser.parseString(json).asJsonObject
        val quoteResponse = root.getAsJsonObject("quoteResponse") ?: return emptyMap()
        val result = quoteResponse.getAsJsonArray("result") ?: return emptyMap()
        val parsed = mutableMapOf<String, MarketQuote>()

        result.forEach { item ->
            val obj = item.asJsonObject
            val rawSymbol = obj.get("symbol")?.asString ?: return@forEach
            val symbol = rawSymbol.substringBefore(".").uppercase(Locale.getDefault())
            val price = obj.get("regularMarketPrice")?.asDouble ?: return@forEach
            val change = obj.get("regularMarketChange")?.asDouble ?: 0.0
            val changePercent = obj.get("regularMarketChangePercent")?.asDouble ?: 0.0
            val dayHigh = obj.get("regularMarketDayHigh")?.asDouble ?: price
            val dayLow = obj.get("regularMarketDayLow")?.asDouble ?: price
            val volume = obj.get("regularMarketVolume")?.asLong ?: 0L
            val name = obj.get("shortName")?.asString ?: symbol

            parsed[symbol] = MarketQuote(
                symbol = symbol,
                companyName = IndianMarketUniverse.companies[symbol] ?: name,
                currentPrice = price,
                change = change,
                changePercent = changePercent,
                dayHigh = dayHigh,
                dayLow = dayLow,
                volume = volume
            )
        }

        return parsed
    }

    private fun simulateQuote(symbol: String, lastPrice: Double): MarketQuote {
        val movementPercent = Random.nextDouble(-0.5, 0.5)
        val newPrice = (lastPrice * (1 + movementPercent / 100.0)).coerceAtLeast(1.0)
        val absoluteMove = newPrice - lastPrice
        val dayRange = (newPrice * 0.012).absoluteValue
        return MarketQuote(
            symbol = symbol,
            companyName = IndianMarketUniverse.companies[symbol] ?: symbol,
            currentPrice = newPrice,
            change = absoluteMove,
            changePercent = movementPercent,
            dayHigh = newPrice + dayRange,
            dayLow = (newPrice - dayRange).coerceAtLeast(1.0),
            volume = Random.nextLong(150_000L, 9_500_000L)
        )
    }

    private fun updateSparkline(quote: MarketQuote): MarketQuote {
        val history = sparkHistory.getOrPut(quote.symbol) { mutableListOf() }
        history.add(quote.currentPrice.toFloat())
        if (history.size > 20) {
            history.removeAt(0)
        }
        return quote.copy(sparkline = history.toList())
    }

    private fun isInternetAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
