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
}

@Composable
fun InvestmentsNavHost(navController: NavHostController = rememberNavController(), startDestination: String = InvestmentRoute.Investment.route) {
    NavHost(navController = navController, startDestination = startDestination) {
        addInvestmentNavGraph()
    }
}

fun NavGraphBuilder.addInvestmentNavGraph() {
    composable(InvestmentRoute.Investment.route) {
        InvestmentScreen()
    }
    composable(InvestmentRoute.Demat.route) { DematScreen() }
    composable(InvestmentRoute.Bonds.route) { BondsScreen() }
}