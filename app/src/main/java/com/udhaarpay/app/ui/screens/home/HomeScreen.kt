package com.udhaarpay.app.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.TapAndPlay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.udhaarpay.app.data.model.MarketNewsItem
import com.udhaarpay.app.ui.navigation.AppRoute
import com.udhaarpay.app.ui.theme.GlassTint
import com.udhaarpay.app.ui.theme.UdhaarPayBrushes
import com.udhaarpay.app.ui.viewmodel.MarketNewsViewModel
import kotlinx.coroutines.delay

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    newsViewModel: MarketNewsViewModel = hiltViewModel()
) {
    val news by newsViewModel.news.collectAsState()

    val actions = listOf(
        QuickAction("Send Money", Icons.AutoMirrored.Filled.Send, AppRoute.SendMoney.route),
        QuickAction("Scan & Pay", Icons.Default.TapAndPlay, AppRoute.ScanPay.route),
        QuickAction("Tickets", Icons.Default.ConfirmationNumber, AppRoute.Tickets.route),
        QuickAction("Invest", Icons.AutoMirrored.Filled.TrendingUp, AppRoute.Invest.route),
        QuickAction("Insurance", Icons.Default.HealthAndSafety, AppRoute.Insurance.route),
        QuickAction("Credit Cards", Icons.Default.CreditCard, AppRoute.CreditCard.route),
        QuickAction("Bill Payments", Icons.AutoMirrored.Filled.ReceiptLong, AppRoute.BillPayments.route),
        QuickAction("My Accounts", Icons.Default.AccountBalance, AppRoute.BankAccounts.route)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(UdhaarPayBrushes.AppBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            HomeNewsCarousel(
                news = news,
                onOpenNews = { onNavigate(AppRoute.InvestNews.route) }
            )
        }

        item {
            WalletSummaryCard()
        }

        item {
            Text(
                "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .height(360.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(actions) { action ->
                    QuickActionCard(
                        action = action,
                        onClick = { onNavigate(action.route) }
                    )
                }
            }
        }

        item {
            Text(
                "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            repeat(3) { index ->
                ActivityCard(
                    title = listOf("UPI Payment", "Recharge", "Ticket Booking")[index],
                    subtitle = listOf("Coffee Shop", "Airtel Prepaid", "Movie")[index],
                    amount = listOf("-INR 210", "-INR 299", "-INR 640")[index]
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeNewsCarousel(
    news: List<MarketNewsItem>,
    onOpenNews: () -> Unit
) {
    val topNews = news.take(8)
    val pagerState = rememberPagerState(pageCount = { if (topNews.isEmpty()) 1 else topNews.size })

    LaunchedEffect(topNews.size) {
        if (topNews.size > 1) {
            while (true) {
                delay(3500L)
                val next = (pagerState.currentPage + 1) % topNews.size
                pagerState.animateScrollToPage(next)
            }
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
    ) {
        if (topNews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(UdhaarPayBrushes.PremiumCard)
                    .padding(16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    "Loading live market headlines...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Box(Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val item = topNews[page]
                    NewsSlide(item = item)
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(topNews.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == pagerState.currentPage) 9.dp else 6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (index == pagerState.currentPage) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    }
                                )
                        )
                    }
                }
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onOpenNews) {
            Text("See more...")
        }
    }
}

@Composable
private fun NewsSlide(item: MarketNewsItem) {
    Box(Modifier.fillMaxSize()) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
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
                            Color.Black.copy(alpha = 0.20f),
                            Color.Black.copy(alpha = 0.72f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
        ) {
            Text(
                item.title,
                maxLines = 2,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                item.source,
                color = Color.White.copy(alpha = 0.86f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun WalletSummaryCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(UdhaarPayBrushes.PremiumCard)
                .padding(18.dp)
        ) {
            Text("Wallet Balance", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(
                "INR 10,000.00",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Today's spending: INR 245",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val tint by animateColorAsState(
        targetValue = if (pressed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
        label = "quick_action_tint"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                pressed = !pressed
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = GlassTint),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                action.icon,
                contentDescription = action.title,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                action.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ActivityCard(
    title: String,
    subtitle: String,
    amount: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Text(amount, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
    }
}
