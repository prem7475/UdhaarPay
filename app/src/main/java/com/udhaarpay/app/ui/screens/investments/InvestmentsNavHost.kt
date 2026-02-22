package com.udhaarpay.app.ui.screens.investments

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

sealed class InvestmentRoute(val route: String) {
    object Investment : InvestmentRoute("investment")
    object Demat : InvestmentRoute("demat")
    object Bonds : InvestmentRoute("bonds")
    object News : InvestmentRoute("news")
    object PaperTrading : InvestmentRoute("paper_trading")
}

@Composable
fun InvestmentsNavHost(navController: NavHostController = rememberNavController(), startDestination: String = InvestmentRoute.Investment.route) {
    NavHost(navController = navController, startDestination = startDestination) {
        addInvestmentNavGraph(navController)
    }
}

fun NavGraphBuilder.addInvestmentNavGraph(navController: NavHostController) {
    composable(InvestmentRoute.Investment.route) {
        InvestmentScreen(
            onOpenDemat = { navController.navigate(InvestmentRoute.Demat.route) },
            onOpenBonds = { navController.navigate(InvestmentRoute.Bonds.route) },
            onOpenNews = { navController.navigate(InvestmentRoute.News.route) },
            onOpenPaperTrading = { navController.navigate(InvestmentRoute.PaperTrading.route) }
        )
    }
    composable(InvestmentRoute.Demat.route) { DematScreen() }
    composable(InvestmentRoute.Bonds.route) { BondsScreen() }
    composable(InvestmentRoute.News.route) { NewsScreen() }
    composable(InvestmentRoute.PaperTrading.route) { PaperTradingScreen() }
}
