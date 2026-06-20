package com.udhaarpay.app.ui.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.udhaarpay.app.ui.screens.bankaccounts.BankAccountScreen
import com.udhaarpay.app.ui.screens.billpayments.BillPaymentScreen
import com.udhaarpay.app.ui.screens.billpayments.MobileRechargeScreen
import com.udhaarpay.app.ui.screens.creditcards.CreditCardScreen
import com.udhaarpay.app.ui.screens.debt.DebtScreen
import com.udhaarpay.app.ui.screens.expenses.ExpenseScreen
import com.udhaarpay.app.ui.screens.home.HomeScreen
import com.udhaarpay.app.ui.screens.insights.InsightsScreen
import com.udhaarpay.app.ui.screens.insurance.InsuranceScreen
import com.udhaarpay.app.ui.screens.investments.InvestmentsNavHost
import com.udhaarpay.app.ui.screens.investments.InvestmentRoute
import com.udhaarpay.app.ui.screens.offers.OffersScreen
import com.udhaarpay.app.ui.screens.payments.PaymentScreen
import com.udhaarpay.app.ui.screens.profile.LinkUPIScreen
import com.udhaarpay.app.ui.screens.profile.ProfileScreen
import com.udhaarpay.app.ui.screens.reminders.RemindersScreen
import com.udhaarpay.app.ui.screens.scanpay.ScanPayScreen
import com.udhaarpay.app.ui.screens.support.SupportScreen
import com.udhaarpay.app.ui.screens.security.SecurityCenterScreen
import com.udhaarpay.app.ui.screens.tickets.TicketScreen
import com.udhaarpay.app.ui.screens.transactions.PassbookScreen
import com.udhaarpay.app.ui.screens.transactions.TransactionsScreen
import com.udhaarpay.app.ui.screens.wallet.WalletManagementScreen
import com.udhaarpay.app.ui.theme.UdhaarPayBrushes
import com.udhaarpay.app.ui.viewmodel.UserProfileViewModel
import com.udhaarpay.app.core.InAppBannerManager
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

private data class BottomItem(
    val title: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UdhaarPayAppShell() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userProfileViewModel: UserProfileViewModel = hiltViewModel()
    val currentUser by userProfileViewModel.currentUser.collectAsState()
    val banner by InAppBannerManager.banner.collectAsState()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(banner?.timestamp) {
        if (banner != null) {
            delay(3200)
            InAppBannerManager.clear()
        }
    }

    val userName = currentUser?.fullName?.ifBlank { "User" } ?: "User"
    val userInitial = userName.firstOrNull()?.uppercase() ?: "U"
    val userEmail = currentUser?.email ?: "user@udhaarpay.com"

    val topElevation by animateDpAsState(
        targetValue = if (drawerState.isOpen) 18.dp else 8.dp,
        label = "top_elevation"
    )

    val drawerItems = listOf(
        DrawerItem("Home", AppRoute.Home.route),
        DrawerItem("Payments", AppRoute.Payments.route),
        DrawerItem("Send / Request", AppRoute.SendMoney.route),
        DrawerItem("Scan & Pay", AppRoute.ScanPay.route),
        DrawerItem("Bill Payments", AppRoute.BillPayments.route),
        DrawerItem("Mobile Recharge", AppRoute.MobileRecharge.route),
        DrawerItem("Invest", AppRoute.Invest.route),
        DrawerItem("Market News", AppRoute.InvestNews.route),
        DrawerItem("Insights", AppRoute.Insights.route),
        DrawerItem("Tickets", AppRoute.Tickets.route),
        DrawerItem("Bookings", AppRoute.Bookings.route),
        DrawerItem("Bank Accounts", AppRoute.BankAccounts.route),
        DrawerItem("Credit Cards", AppRoute.CreditCard.route),
        DrawerItem("Insurance", AppRoute.Insurance.route),
        DrawerItem("Debt", AppRoute.Debt.route),
        DrawerItem("Expenses", AppRoute.Expenses.route),
        DrawerItem("Offers", AppRoute.Offers.route),
        DrawerItem("Transactions", AppRoute.Transactions.route),
        DrawerItem("Profile", AppRoute.Profile.route),
        DrawerItem("Link UPI", AppRoute.LinkUpi.route),
        DrawerItem("Reminders", AppRoute.Reminders.route),
        DrawerItem("Wallet", AppRoute.WalletManagement.route),
        DrawerItem("Security", AppRoute.Security.route),
        DrawerItem("Support", AppRoute.Support.route)
    )

    val bottomItems = listOf(
        BottomItem("Home", AppRoute.Home.route, Icons.Default.Home),
        BottomItem("Cards", AppRoute.CreditCard.route, Icons.Default.CreditCard),
        BottomItem("Scan & Pay", AppRoute.ScanPay.route, Icons.Default.Contactless),
        BottomItem("Debt", AppRoute.Debt.route, Icons.Default.CurrencyExchange),
        BottomItem("Profile", AppRoute.Profile.route, Icons.Default.Person)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .background(UdhaarPayBrushes.AppBackground)
                    .padding(18.dp)
            ) {
                Spacer(Modifier.size(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.size(54.dp)
                    ) {
                        if (!currentUser?.profilePhotoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = currentUser?.profilePhotoUrl,
                                contentDescription = "Profile photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(userInitial, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(userName, fontWeight = FontWeight.SemiBold)
                        Text(userEmail, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.size(22.dp))
                drawerItems.forEach { item ->
                    Text(
                        item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                }
                                scope.launch { drawerState.close() }
                            }
                            .padding(horizontal = 10.dp, vertical = 12.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) {
            Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                "UdhaarPay",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    navigationIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.size(34.dp)
                            ) {
                                if (!currentUser?.profilePhotoUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = currentUser?.profilePhotoUrl,
                                        contentDescription = "Profile photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(userInitial, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(
                                userName,
                                fontSize = 12.sp,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                navController.navigate(AppRoute.ScanPay.route) {
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(Icons.AutoMirrored.Filled.Message, contentDescription = "Contacts & pay")
                                }
                            }
                        }
                    },
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            },
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                Surface(
                    shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
                    shadowElevation = topElevation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp
                    ) {
                        bottomItems.forEach { item ->
                            val selected = when (item.route) {
                                AppRoute.Profile.route -> currentRoute in listOf(AppRoute.Profile.route, AppRoute.Account.route)
                                AppRoute.Debt.route -> currentRoute in listOf(AppRoute.Debt.route, AppRoute.Expenses.route)
                                else -> currentRoute == item.route
                            }
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    navController.navigate(item.route) {
                                        launchSingleTop = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                icon = { Icon(item.icon, contentDescription = item.title, modifier = Modifier.size(22.dp)) },
                                label = { Text(item.title, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    AppNavHost(navController = navController)
                }
                AnimatedVisibility(
                    visible = banner != null,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp, start = 12.dp, end = 12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        tonalElevation = 8.dp,
                        color = if (banner?.isCredit == true) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(
                                banner?.title.orEmpty(),
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                banner?.message.orEmpty(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home.route
    ) {
        composable(AppRoute.Home.route) {
            HomeScreen(onNavigate = { navController.navigate(it) })
        }
        composable(AppRoute.Payments.route) {
            PaymentScreen(onNavigate = { navController.navigate(it) })
        }
        composable(AppRoute.SendMoney.route) {
            PaymentScreen(onNavigate = { navController.navigate(it) })
        }
        composable(AppRoute.RequestMoney.route) {
            PaymentScreen(onNavigate = { navController.navigate(it) })
        }
        composable(AppRoute.ScanPay.route) {
            ScanPayScreen(
                onNavigateHome = {
                    navController.navigate(AppRoute.Home.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(AppRoute.BillPayments.route) { BillPaymentScreen() }
        composable(AppRoute.MobileRecharge.route) { MobileRechargeScreen() }
        composable(AppRoute.Invest.route) { InvestmentsNavHost() }
        composable(AppRoute.InvestNews.route) {
            InvestmentsNavHost(startDestination = InvestmentRoute.News.route)
        }
        composable(AppRoute.Insights.route) { InsightsScreen() }
        composable(AppRoute.Tickets.route) { TicketScreen() }
        composable(AppRoute.Bookings.route) { TicketScreen() }
        composable(AppRoute.CreditCard.route) {
            CreditCardScreen(
                onNavigateHome = {
                    navController.navigate(AppRoute.Home.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(AppRoute.BankAccounts.route) {
            BankAccountScreen(
                onOpenPassbook = { accountId ->
                    navController.navigate(AppRoute.passbook(accountId))
                }
            )
        }
        composable(AppRoute.Insurance.route) { InsuranceScreen() }
        composable(AppRoute.Debt.route) { DebtScreen() }
        composable(AppRoute.Expenses.route) { ExpenseScreen() }
        composable(AppRoute.Profile.route) { ProfileScreen() }
        composable(AppRoute.LinkUpi.route) { LinkUPIScreen() }
        composable(AppRoute.WalletManagement.route) { WalletManagementScreen() }
        composable(AppRoute.Security.route) { SecurityCenterScreen() }
        composable(AppRoute.Reminders.route) { RemindersScreen() }
        composable(AppRoute.Support.route) { SupportScreen() }
        composable(AppRoute.Transactions.route) { TransactionsScreen() }
        composable(AppRoute.Offers.route) { OffersScreen() }
        composable(
            route = AppRoute.Passbook.route,
            arguments = listOf(navArgument("accountId") { type = NavType.LongType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getLong("accountId") ?: -1L
            PassbookScreen(accountId = accountId)
        }
        composable(AppRoute.Account.route) { ProfileScreen() }
    }
}
