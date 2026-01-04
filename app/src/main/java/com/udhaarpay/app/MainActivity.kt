package com.udhaarpay.app

import com.udhaarpay.app.ui.screens.support.SupportScreen
import com.udhaarpay.app.ui.screens.reminders.RemindersScreen
import com.udhaarpay.app.ui.screens.offers.OffersScreen
import com.udhaarpay.app.ui.screens.transactions.TransactionsScreen
import com.udhaarpay.app.ui.screens.scanpay.ScanPayScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material3.*
import androidx.compose.ui.graphics.luminance
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.udhaarpay.app.ui.screens.investments.InvestmentsNavHost
import com.udhaarpay.app.ui.screens.creditcards.CreditCardScreen
import com.udhaarpay.app.ui.screens.bankaccounts.BankAccountScreen
import com.udhaarpay.app.ui.screens.insurance.InsuranceScreen
import com.udhaarpay.app.ui.screens.debt.DebtScreen
import com.udhaarpay.app.ui.screens.profile.ProfileScreen
import com.udhaarpay.app.ui.screens.wallet.WalletManagementScreen

@Composable
fun UdhaarPayAmexTheme(content: @Composable () -> Unit) {
    val AmexBlue = Color(0xFF016FD0)
    val AmexBlueDark = Color(0xFF003366)
    val AmexGradient = Brush.verticalGradient(listOf(AmexBlue, AmexBlueDark))
    val AmexAccent = Color(0xFF00C6D7)
    val AmexSurface = Color(0xFF112244)
    val AmexCard = Color(0xFF1A2A4C)
    val AmexOutline = Color(0xFF3B82F6)
    val amexColors = darkColorScheme(
        primary = AmexBlue,
        onPrimary = Color.White,
        secondary = AmexAccent,
        onSecondary = Color.White,
        background = AmexBlueDark,
        onBackground = Color.White,
        surface = AmexSurface,
        onSurface = Color.White,
        surfaceVariant = AmexCard,
        onSurfaceVariant = Color(0xFFCBD5E1),
        outline = AmexOutline
    )
    MaterialTheme(
        colorScheme = amexColors,
        typography = Typography(),
        content = content
    )
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainAppContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent() {
    UdhaarPayAmexTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf(
            "Home", "Invest", "Pay", "History", "Profile"
        )
        val tabRoutes = listOf(
            "dashboard", "investments", "scan_pay", "transactions", "profile"
        )
        val drawerAnimProgress = animateFloatAsState(
            if (drawerState.isOpen) 1f else 0f,
            label = "drawerAnim"
        )
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = 0.95f + 0.05f * drawerAnimProgress.value
                            scaleY = 0.95f + 0.05f * drawerAnimProgress.value
                            alpha = 0.7f + 0.3f * drawerAnimProgress.value
                            shadowElevation = 24f * drawerAnimProgress.value
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(290.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(Color(0xFF016FD0), Color(0xFF003366))
                                )
                            )
                            .padding(top = 48.dp, start = 18.dp, end = 18.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // User Info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF00C6D7),
                                border = BorderStroke(2.dp, Color.White),
                                modifier = Modifier.size(56.dp)
                            ) {
                                // Placeholder for user photo
                                Box(Modifier.fillMaxSize())
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text("Amit Sharma", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                                Text("amit.sharma@email.com", fontSize = 13.sp, color = Color(0xFFB6E6F7))
                            }
                        }
                        Spacer(Modifier.height(32.dp))
                        val drawerItems = listOf(
                            "Dashboard" to "dashboard",
                            "Investments" to "investments",
                            "Credit Cards" to "credit_card",
                            "Bank Accounts" to "bank_accounts",
                            "Insurance" to "insurance",
                            "Debt" to "debt",
                            "Profile" to "profile",
                            "Wallets" to "wallet_management",
                            "Reminders" to "reminders",
                            "Support" to "support",
                            "Transactions" to "transactions",
                            "Offers" to "offers",
                            "Scan & Pay" to "scan_pay"
                        )
                        drawerItems.forEach { (label, route) ->
                            Text(
                                label,
                                fontSize = 16.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 8.dp)
                                    .background(
                                        color = Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        scope.launch { drawerState.close() }
                                    }
                            )
                        }
                    }
                }
            },
        ) {
            val topBarElevation = animateDpAsState(
                if (drawerState.isOpen) 16.dp else 8.dp,
                label = "topBarElevation"
            )
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    SmallTopAppBar(
                        title = {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(
                                    "UdhaarPay",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    letterSpacing = 2.sp
                                )
                            }
                        },
                        navigationIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF1E293B),
                                    border = BorderStroke(2.dp, Color(0xFF6366F1)),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(Modifier.fillMaxSize())
                                }
                            }
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.shadow(topBarElevation.value, RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp), clip = false)
                    )
                },
                bottomBar = {
                    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                        tabs.forEachIndexed { idx, label ->
                            NavigationBarItem(
                                selected = selectedTab == idx,
                                onClick = {
                                    selectedTab = idx
                                    navController.navigate(tabRoutes[idx]) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    when (label) {
                                        "Home" -> Icon(Icons.Default.AccountBalance, contentDescription = "Home")
                                        "Invest" -> Icon(Icons.Default.CreditCard, contentDescription = "Invest")
                                        "Pay" -> Icon(Icons.Default.Payment, contentDescription = "Pay")
                                        "History" -> Icon(Icons.Default.LocalAtm, contentDescription = "History")
                                        "Profile" -> Icon(Icons.Default.Group, contentDescription = "Profile")
                                        else -> Icon(Icons.Default.AccountBalance, contentDescription = label)
                                    }
                                },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                    ) {
                        composable("dashboard") {
                            UdhaarPayDashboard(
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }
                        composable("investments") { InvestmentsNavHost() }
                        composable("credit_card") { CreditCardScreen() }
                        composable("bank_accounts") { BankAccountScreen() }
                        composable("insurance") { InsuranceScreen() }
                        composable("debt") { DebtScreen() }
                        composable("profile") { ProfileScreen() }
                        composable("wallet_management") { WalletManagementScreen() }
                        composable("reminders") { RemindersScreen() }
                        composable("support") { SupportScreen() }
                        composable("transactions") { TransactionsScreen() }
                        composable("offers") { OffersScreen() }
                        composable("scan_pay") { ScanPayScreen() }
                    }
                }
            }
        }
    }
}

data class PaymentOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun UdhaarPayDashboard(modifier: Modifier = Modifier, onNavigate: (String) -> Unit) {
    // Modern card-based dashboard layout
    val paymentOptions = listOf(
        PaymentOption(
            "investments",
            "Investments",
            "Manage and invest in assets",
            Icons.Default.AccountBalance,
            Color(0xFF22C55E)
        ),
        PaymentOption(
            "scan_pay",
            "Scan & Pay",
            "Scan QR or add from gallery",
            Icons.Default.Payment,
            Color(0xFF2563EB)
        ),
        PaymentOption(
            "send_money",
            "Send Money",
            "Transfer funds to friends & family",
            Icons.Default.SendToMobile,
            Color(0xFF2563EB)
        ),
        PaymentOption(
            "request_money",
            "Request Money",
            "Ask for payment from others",
            Icons.Default.AccountBalance,
            Color(0xFF7C3AED)
        ),
        PaymentOption(
            "add_money",
            "Add Money",
            "Load funds to your wallet",
            Icons.Default.LocalAtm,
            Color(0xFF059669)
        ),
        PaymentOption(
            "pay_bills",
            "Pay Bills",
            "Pay utilities & other services",
            Icons.Default.Payment,
            Color(0xFFDC2626)
        ),
        PaymentOption(
            "mobile_recharge",
            "Mobile Recharge",
            "Recharge mobile & data plans",
            Icons.Default.PhonelinkSetup,
            Color(0xFFF59E0B)
        ),
        PaymentOption(
            "cashback",
            "Cashback Offers",
            "View available cashback deals",
            Icons.Default.LocalOffer,
            Color(0xFF06B6D4)
        ),
        PaymentOption(
            "credit_card",
            "Credit Card",
            "Manage your credit cards",
            Icons.Default.CreditCard,
            Color(0xFFEC4899)
        ),
        PaymentOption(
            "profile",
            "Profile",
            "View and edit your profile",
            Icons.Default.Group,
            Color(0xFF7C3AED)
        ),
        PaymentOption(
            "wallet_management",
            "Wallets",
            "Manage multiple wallets",
            Icons.Default.LocalAtm,
            Color(0xFF059669)
        ),
        PaymentOption(
            "transactions",
            "Transactions",
            "View all your transaction history",
            Icons.Default.Payment,
            Color(0xFF0EA5E9)
        ),
        PaymentOption(
            "offers",
            "Offers",
            "Latest offers and rewards",
            Icons.Default.LocalOffer,
            Color(0xFFF59E0B)
        ),
        PaymentOption(
            "reminders",
            "Bill Reminders",
            "Never miss a bill payment",
            Icons.Default.PhonelinkSetup,
            Color(0xFF6366F1)
        ),
        PaymentOption(
            "support",
            "Support",
            "Get help & support",
            Icons.Default.Group,
            Color(0xFFDC2626)
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(bottomEnd = 32.dp, bottomStart = 32.dp),
            color = MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp)
            ) {
                Text(
                    "UdhaarPay",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Your Digital Payment Solution",
                    fontSize = 15.sp,
                    color = Color(0xFFD1D5DB),
                    modifier = Modifier.padding(top = 4.dp)
                )
                // Wallet Balance
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 6.dp,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "Wallet Balance",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "₹5,450.00",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
        // Payment Options Grid
        Spacer(Modifier.height(18.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(paymentOptions.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowItems.forEach { option ->
                        PaymentOptionCard(
                            option = option,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    when (option.id) {
                                        "investments" -> onNavigate("investments")
                                        "scan_pay" -> onNavigate("scan_pay")
                                        "credit_card" -> onNavigate("credit_card")
                                        "bank_accounts" -> onNavigate("bank_accounts")
                                        "insurance" -> onNavigate("insurance")
                                        "debt" -> onNavigate("debt")
                                        "profile" -> onNavigate("profile")
                                        "wallet_management" -> onNavigate("wallet_management")
                                        "transactions" -> onNavigate("transactions")
                                        "offers" -> onNavigate("offers")
                                        "reminders" -> onNavigate("reminders")
                                        "support" -> onNavigate("support")
                                        else -> {}
                                    }
                                }
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentOptionCard(
    option: PaymentOption,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 8.dp)
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                tint = option.color,
                modifier = Modifier.size(38.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                option.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            Text(
                option.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun PaymentDetailScreen(
    option: PaymentOption,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        // Back Button
        Text(
            "← Back",
            color = option.color,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable { onBack() }
                .padding(8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Feature Icon & Title
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = option.color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                tint = option.color,
                modifier = Modifier.size(60.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            option.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Text(
            option.description,
            fontSize = 14.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Feature List
        listOf(
            "Fast and secure transactions",
            "Real-time notifications",
            "Transaction history tracking",
            "24/7 customer support",
            "Multiple payment methods"
        ).forEach { feature ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(option.color, shape = RoundedCornerShape(50.dp))
                )
                Text(
                    feature,
                    fontSize = 14.sp,
                    color = Color(0xFFE2E8F0),
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Action Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(option.color, shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Get Started",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
