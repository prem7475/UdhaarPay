package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paper_trading_account")
data class PaperTradingAccount(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val virtualBalance: Double = 100000.0,
    val totalInvested: Double = 0.0,
    val totalProfitLoss: Double = 0.0,
    val createdDate: Long = System.currentTimeMillis()
)

