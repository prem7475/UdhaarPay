package com.udhaarpay.app.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TapAndPlay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.udhaarpay.app.ui.components.PremiumActionCard
import com.udhaarpay.app.ui.components.PremiumMetricCard
import com.udhaarpay.app.ui.components.PremiumScreen
import com.udhaarpay.app.ui.components.PremiumSectionHeader
import com.udhaarpay.app.ui.navigation.AppRoute
import com.udhaarpay.app.ui.theme.GlassTint
import com.udhaarpay.app.ui.theme.UdhaarPayBrushes
import com.udhaarpay.app.ui.viewmodel.BankAccountViewModel
import com.udhaarpay.app.ui.viewmodel.CreditCardViewModel
import com.udhaarpay.app.ui.viewmodel.DebtViewModel
import com.udhaarpay.app.ui.viewmodel.MarketNewsViewModel
import com.udhaarpay.app.ui.viewmodel.PaperTradingViewModel
import com.udhaarpay.app.ui.viewmodel.UserProfileViewModel
import com.udhaarpay.app.ui.viewmodel.WalletViewModel
import kotlinx.coroutines.delay

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    newsViewModel: MarketNewsViewModel = hiltViewModel(),
    userViewModel: UserProfileViewModel = hiltViewModel(),
    bankAccountViewModel: BankAccountViewModel = hiltViewModel(),
    creditCardViewModel: CreditCardViewModel = hiltViewModel(),
    walletViewModel: WalletViewModel = hiltViewModel(),
    debtViewModel: DebtViewModel = hiltViewModel(),
    paperTradingViewModel: PaperTradingViewModel = hiltViewModel()
) {
    val news by newsViewModel.news.collectAsState()
    val user by userViewModel.currentUser.collectAsState()
    val bankAccounts by bankAccountViewModel.accounts.collectAsState()
    val cards by creditCardViewModel.creditCards.collectAsState()
    val wallet by walletViewModel.walletAccount.collectAsState()
    val totalGiven by debtViewModel.totalGiven.collectAsState()
    val totalTaken by debtViewModel.totalTaken.collectAsState()
    val paperSummary by paperTradingViewModel.portfolioSummary.collectAsState()

    val actions = listOf(
        QuickAction("Send Money", Icons.AutoMirrored.Filled.Send, AppRoute.SendMoney.route),
        QuickAction("Scan & Pay", Icons.Default.TapAndPlay, AppRoute.ScanPay.route),
        QuickAction("Invest", Icons.AutoMirrored.Filled.TrendingUp, AppRoute.Invest.route),
        QuickAction("Insights", Icons.AutoMirrored.Filled.ReceiptLong, AppRoute.Insights.route),
        QuickAction("Cards", Icons.Default.CreditCard, AppRoute.CreditCard.route),
        QuickAction("Bank", Icons.Default.AccountBalance, AppRoute.BankAccounts.route),
        QuickAction("Help Desk", Icons.AutoMirrored.Filled.HelpOutline, AppRoute.Support.route),
        QuickAction("Security", Icons.Default.Settings, AppRoute.Security.route),
        QuickAction("Debt", Icons.Default.Receipt, AppRoute.Debt.route),
        QuickAction("Bookings", Icons.Default.Sell, AppRoute.Bookings.route),
        QuickAction("Profile", Icons.Default.Person, AppRoute.Profile.route),
        QuickAction("Insurance", Icons.Default.HealthAndSafety, AppRoute.Insurance.route)
    )

    val primaryAccounts = remember(bankAccounts) {
        bankAccounts.filter { !it.accountType.equals("Wallet", true) && !it.accountType.equals("Cash", true) }
    }
    val bankBalance = primaryAccounts.sumOf { it.balance }
    val walletBalance = wallet?.balance ?: bankAccounts.firstOrNull { it.accountType.equals("Wallet", true) }?.balance ?: 0.0
    val cardOutstanding = cards.sumOf { (it.limit - it.balance).coerceAtLeast(0.0) }
    val netAfterDues = bankBalance + walletBalance + paperSummary.totalPortfolioValue + totalGiven - totalTaken - cardOutstanding

    PremiumScreen {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                HeroCard(
                    userName = user?.fullName?.ifBlank { "Peter" } ?: "Peter",
                    userInitial = user?.fullName?.firstOrNull()?.uppercase() ?: "U",
                    bankBalance = bankBalance,
                    walletBalance = walletBalance,
                    paperValue = paperSummary.totalPortfolioValue,
                    netAfterDues = netAfterDues,
                    onNavigate = onNavigate
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    PremiumMetricCard(
                        title = "Cards Due",
                        value = "INR ${"%,.0f".format(cardOutstanding)}",
                        subtitle = "Minimum due tracked",
                        modifier = Modifier.fillMaxWidth(0.47f)
                    )
                    PremiumMetricCard(
                        title = "Debts",
                        value = "INR ${"%,.0f".format(totalTaken)}",
                        subtitle = "Money to repay",
                        modifier = Modifier.fillMaxWidth(0.47f)
                    )
                }
            }

            item {
                PremiumMetricCard(
                    title = "Paper Trading",
                    value = "INR ${"%,.0f".format(paperSummary.totalPortfolioValue)}",
                    subtitle = "Learning balance starts at 10 lakhs",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                PremiumSectionHeader(
                    title = "Quick Actions",
                    subtitle = "Payments, investing, help, and account controls"
                )
                Spacer(Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(560.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(actions, key = { it.route }) { action ->
                        PremiumActionCard(
                            title = action.title,
                            subtitle = when (action.route) {
                                AppRoute.ScanPay.route -> "Tap, scan, or pay"
                                AppRoute.Invest.route -> "Stocks, news, paper trade"
                                AppRoute.Support.route -> "Help desk and policy"
                                else -> "Open section"
                            },
                            onClick = { onNavigate(action.route) },
                            leadingContent = {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = action.icon,
                                            contentDescription = action.title,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }

            item {
                HomeNewsCarousel(
                    news = news,
                    onOpenNews = { onNavigate(AppRoute.InvestNews.route) }
                )
            }

            item {
                PremiumSectionHeader(
                    title = "Recent Activity",
                    subtitle = "Recent local transactions, tickets, and transfers"
                )
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    RecentActivityCard("UPI Payment", "Coffee Shop", "-INR 210", "Just now")
                    RecentActivityCard("Recharge", "Airtel Prepaid", "-INR 299", "Today")
                    RecentActivityCard("Ticket Booking", "Movie", "-INR 640", "Yesterday")
                    RecentActivityCard("Debt Entry", "Salary advance", "+INR 8,000", "2 days ago")
                }
            }

            item {
                PremiumActionCard(
                    title = "Help Desk, Versions & Terms",
                    subtitle = "Open support, app info, and policy pages in one place",
                    onClick = { onNavigate(AppRoute.Support.route) },
                    leadingContent = {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                )
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

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PremiumSectionHeader(
            title = "Market Headlines",
            subtitle = "Fresh stories in a quiet premium frame",
            actionText = "See all",
            onAction = onOpenNews
        )
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
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
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        repeat(topNews.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == pagerState.currentPage) 10.dp else 6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (index == pagerState.currentPage) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            Color.White.copy(alpha = 0.35f)
                                        }
                                    )
                            )
                        }
                    }
                }
            }
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
                            Color.Black.copy(alpha = 0.18f),
                            Color.Black.copy(alpha = 0.78f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                item.title,
                maxLines = 2,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                item.source,
                color = Color.White.copy(alpha = 0.84f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun HeroCard(
    userName: String,
    userInitial: String,
    bankBalance: Double,
    walletBalance: Double,
    paperValue: Double,
    netAfterDues: Double,
    onNavigate: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(UdhaarPayBrushes.PremiumCard)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.82f)) {
                    Text("Good day, $userName", color = Color.White.copy(alpha = 0.86f), fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Welcome to your wallet",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(userInitial, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            PremiumMetricCard(
                title = "Available Balance",
                value = "INR ${"%,.0f".format(bankBalance + walletBalance + paperValue)}",
                subtitle = "Bank + wallet + paper trading",
                modifier = Modifier.fillMaxWidth(),
                accent = Brush.linearGradient(
                    listOf(
                        Color(0xFF2A2210),
                        Color(0xFF64501A),
                        Color(0xFFB58D2F)
                    )
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                PremiumMetricCard(
                    title = "Paper Net",
                    value = "INR ${"%,.0f".format(paperValue)}",
                    subtitle = "Learning capital",
                    modifier = Modifier.fillMaxWidth(0.47f)
                )
                PremiumMetricCard(
                    title = "Net After Dues",
                    value = "INR ${"%,.0f".format(netAfterDues)}",
                    subtitle = if (netAfterDues >= 0) "Healthy runway" else "Dues exceed cash",
                    modifier = Modifier.fillMaxWidth(0.47f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                PremiumActionCard(
                    title = "Tap to Pay",
                    subtitle = "Open NFC payments",
                    modifier = Modifier.fillMaxWidth(0.49f),
                    onClick = { onNavigate(AppRoute.ScanPay.route) },
                    leadingContent = {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(Icons.Default.TapAndPlay, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                )
                PremiumActionCard(
                    title = "View Insights",
                    subtitle = "Spending and dues",
                    modifier = Modifier.fillMaxWidth(0.49f),
                    onClick = { onNavigate(AppRoute.Insights.route) },
                    leadingContent = {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RecentActivityCard(
    title: String,
    subtitle: String,
    amount: String,
    time: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassTint),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (amount.startsWith("-")) MaterialTheme.colorScheme.error.copy(alpha = 0.14f)
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        title.firstOrNull()?.uppercase() ?: "U",
                        fontWeight = FontWeight.Bold,
                        color = if (amount.startsWith("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }
            Column(modifier = Modifier.fillMaxWidth(0.68f)) {
                Text(title, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                Text(time, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
            Text(
                amount,
                color = if (amount.startsWith("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
