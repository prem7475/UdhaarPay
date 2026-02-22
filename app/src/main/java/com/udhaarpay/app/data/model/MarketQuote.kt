package com.udhaarpay.app.data.model

data class MarketQuote(
    val symbol: String,
    val companyName: String,
    val currentPrice: Double,
    val change: Double,
    val changePercent: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val volume: Long,
    val sparkline: List<Float> = emptyList()
)

data class HoldingItem(
    val symbol: String,
    val companyName: String,
    val quantity: Int,
    val avgEntry: Double,
    val currentPrice: Double,
    val pnl: Double
)

data class PortfolioSummary(
    val totalPortfolioValue: Double,
    val totalProfitLoss: Double,
    val todayProfitLoss: Double,
    val totalInvested: Double
)

