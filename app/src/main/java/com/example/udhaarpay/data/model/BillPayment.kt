package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BillCategory {
    ELECTRICITY, WATER, GAS, BROADBAND, DTH
}

data class BillProvider(
    val category: BillCategory,
    val name: String,
    val website: String,
    val icon: String
)

@Entity(tableName = "bills")
data class BillPayment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val category: BillCategory,
    val provider: String,
    val amount: Double,
    val status: TransactionStatus = TransactionStatus.SUCCESS,
    val timestamp: Long = System.currentTimeMillis()
)

val billProviders = mapOf(
    BillCategory.ELECTRICITY to listOf(
        BillProvider(BillCategory.ELECTRICITY, "TATA Power", "https://www.tatapower.com", "⚡"),
        BillProvider(BillCategory.ELECTRICITY, "NTPC", "https://www.ntpconline.com", "⚡")
    ),
    BillCategory.WATER to listOf(
        BillProvider(BillCategory.WATER, "MWSSB", "https://www.mwssb.gov.in", "💧"),
        BillProvider(BillCategory.WATER, "BWSSB", "https://www.bwssb.org", "💧")
    ),
    BillCategory.GAS to listOf(
        BillProvider(BillCategory.GAS, "IGL", "https://www.iglonline.com", "🔥"),
        BillProvider(BillCategory.GAS, "GAIL", "https://www.gailonline.com", "🔥")
    ),
    BillCategory.BROADBAND to listOf(
        BillProvider(BillCategory.BROADBAND, "JIO Fiber", "https://www.jiofiber.com", "📡"),
        BillProvider(BillCategory.BROADBAND, "Airtel Fiber", "https://www.airtel.in", "📡")
    ),
    BillCategory.DTH to listOf(
        BillProvider(BillCategory.DTH, "Tata Play", "https://www.tataplay.com", "📺"),
        BillProvider(BillCategory.DTH, "Dish TV", "https://www.dishtv.in", "📺")
    )
)
