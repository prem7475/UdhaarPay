package com.udhaarpay.app.ui.screens.investments

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.udhaarpay.app.data.model.MarketNewsItem
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.components.UdhaarPayTextButton
import com.udhaarpay.app.ui.screens.common.InAppBrowserDialog
import com.udhaarpay.app.ui.viewmodel.MarketNewsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NewsScreen(viewModel: MarketNewsViewModel = hiltViewModel()) {
    val news by viewModel.news.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val status by viewModel.statusMessage.collectAsState()
    val lastUpdatedAt by viewModel.lastUpdatedAt.collectAsState()
    var selectedArticle by remember { mutableStateOf<MarketNewsItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Live Market News", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Trending headlines with real-time images",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            UdhaarPayButton(
                text = "Refresh",
                onClick = { viewModel.refreshNews() },
                enabled = !isLoading,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (lastUpdatedAt != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                "Updated: " + SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(lastUpdatedAt ?: 0L)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (!status.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(status ?: "", color = MaterialTheme.colorScheme.primary)
            UdhaarPayTextButton(text = "Dismiss", onClick = { viewModel.clearStatus() })
        }

        if (isLoading && news.isEmpty()) {
            Spacer(Modifier.height(18.dp))
            CircularProgressIndicator()
        } else {
            Spacer(Modifier.height(10.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items = news, key = { "${it.link}-${it.publishedAtMillis}" }) { article ->
                    NewsCard(
                        article = article,
                        onOpen = { selectedArticle = article }
                    )
                }
            }
        }
    }

    selectedArticle?.let { article ->
        InAppBrowserDialog(
            title = article.source,
            url = article.link,
            onDismiss = { selectedArticle = null }
        )
    }
}

@Composable
private fun NewsCard(
    article: MarketNewsItem,
    onOpen: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        onClick = onOpen
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = article.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.55f)
                                )
                            )
                        )
                )
                Text(
                    article.source,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(article.title, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                article.summary?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    DateUtils.getRelativeTimeSpanString(article.publishedAtMillis).toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
