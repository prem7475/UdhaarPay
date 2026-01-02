package com.example.udhaarpay.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.udhaarpay.ui.screens.analytics.AnalyticsScreen
import com.example.udhaarpay.ui.screens.auth.AuthScreen
import com.example.udhaarpay.ui.screens.home.HomeScreen
import com.example.udhaarpay.ui.screens.offers.OffersScreen
import com.example.udhaarpay.ui.screens.profile.ProfileScreen
import com.example.udhaarpay.ui.screens.scanpay.ScanPayScreen
import com.example.udhaarpay.ui.screens.services.ServicesScreen
import com.example.udhaarpay.ui.screens.services.SIPInvestmentScreen
import com.example.udhaarpay.ui.screens.services.OpenDematScreen
import com.example.udhaarpay.ui.screens.services.CreditScoreScreen
import com.example.udhaarpay.ui.screens.nfc.NFCPaymentScreen
import com.example.udhaarpay.ui.screens.transactions.TransactionHistoryScreen
import com.example.udhaarpay.ui.screens.wallet.WalletScreen
import com.example.udhaarpay.ui.viewmodel.AuthViewModel

sealed class Route(val route: String) {
    object Auth : Route("auth")
    object Home : Route("home")
    object Wallet : Route("wallet")
    object ScanPay : Route("scan_pay")
    object Transactions : Route("transactions")
    object Offers : Route("offers")
    object Services : Route("services")
    object Profile : Route("profile")
    object Analytics : Route("analytics")
    object NFC : Route("nfc")
    object SIPInvestment : Route("sip_investment")
    object OpenDemat : Route("open_demat")
    object CreditScore : Route("credit_score")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authSuccess = authViewModel.authSuccess.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (authSuccess.value) Route.Home.route else Route.Auth.route
    ) {
        composable(Route.Auth.route) {
            AuthScreen(onAuthSuccess = {
                navController.navigate(Route.Home.route) {
                    popUpTo(Route.Auth.route) { inclusive = true }
                }
            })
        }

        composable(Route.Home.route) {
            HomeScreen(
                onNavigateToWallet = {
                    navController.navigate(Route.Wallet.route)
                },
                onNavigateToScan = {
                    navController.navigate(Route.ScanPay.route)
                },
                onNavigateToTransactions = {
                    navController.navigate(Route.Transactions.route)
                },
                onNavigateToOffers = {
                    navController.navigate(Route.Offers.route)
                },
                onNavigateToServices = {
                    navController.navigate(Route.Services.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Route.Profile.route)
                },
                onNavigateToAnalytics = {
                    navController.navigate(Route.Analytics.route)
                }
            )
        }

        composable(Route.Wallet.route) {
            WalletScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.ScanPay.route) {
            ScanPayScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.Transactions.route) {
            TransactionHistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.Offers.route) {
            OffersScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.Services.route) {
            ServicesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Route.Auth.route) {
                        popUpTo(Route.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Analytics.route) {
            AnalyticsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.SIPInvestment.route) {
            SIPInvestmentScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.OpenDemat.route) {
            OpenDematScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.CreditScore.route) {
            CreditScoreScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.NFC.route) {
            NFCPaymentScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
