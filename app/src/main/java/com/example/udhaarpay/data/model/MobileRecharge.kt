package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RechargeOperator {
    JIO, AIRTEL, VODAFONE, IDEA
}

data class RechargePlan(
    val planId: String,
    val amount: Double,
    val validity: String,
    val data: String,
    val talkTime: String
)

@Entity(tableName = "recharges")
data class MobileRecharge(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val phoneNumber: String,
    val operator: RechargeOperator,
    val planAmount: Double,
    val planId: String,
    val status: TransactionStatus = TransactionStatus.SUCCESS,
    val timestamp: Long = System.currentTimeMillis()
)

val mockRechargePlans = mapOf(
    RechargeOperator.JIO to listOf(
        RechargePlan("JIO_29", 29.0, "28 days", "6GB", "Unlimited"),
        RechargePlan("JIO_49", 49.0, "28 days", "12GB", "Unlimited"),
        RechargePlan("JIO_99", 99.0, "56 days", "50GB", "Unlimited"),
        RechargePlan("JIO_199", 199.0, "84 days", "150GB", "Unlimited")
    ),
    RechargeOperator.AIRTEL to listOf(
        RechargePlan("AIR_29", 29.0, "28 days", "6GB", "Unlimited"),
        RechargePlan("AIR_49", 49.0, "28 days", "12GB", "Unlimited"),
        RechargePlan("AIR_99", 99.0, "56 days", "50GB", "Unlimited"),
        RechargePlan("AIR_199", 199.0, "84 days", "150GB", "Unlimited")
    ),
    RechargeOperator.VODAFONE to listOf(
        RechargePlan("VOD_29", 29.0, "28 days", "6GB", "Unlimited"),
        RechargePlan("VOD_49", 49.0, "28 days", "12GB", "Unlimited"),
        RechargePlan("VOD_99", 99.0, "56 days", "50GB", "Unlimited"),
        RechargePlan("VOD_199", 199.0, "84 days", "150GB", "Unlimited")
    ),
    RechargeOperator.IDEA to listOf(
        RechargePlan("IDEA_29", 29.0, "28 days", "6GB", "Unlimited"),
        RechargePlan("IDEA_49", 49.0, "28 days", "12GB", "Unlimited"),
        RechargePlan("IDEA_99", 99.0, "56 days", "50GB", "Unlimited"),
        RechargePlan("IDEA_199", 199.0, "84 days", "150GB", "Unlimited")
    )
)
