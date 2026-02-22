package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class Trade(
    @PrimaryKey(autoGenerate = true) val tradeId: Long = 0L,
    val stockSymbol: String,
    val companyName: String,
    val tradeType: String, // BUY, SELL, CALL, PUT
    val quantity: Int,
    val entryPrice: Double,
    val currentPrice: Double,
    val profitLoss: Double,
    val tradeStatus: String, // OPEN, CLOSED
    val timestamp: Long
)

