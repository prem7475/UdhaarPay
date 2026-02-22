package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey(autoGenerate = true) val debtId: Long = 0L,
    val personName: String,
    val amount: Double,
    val type: String, // given/taken
    val category: String = "Other",
    val paymentSource: String = "Wallet", // Bank/Card/Wallet
    val accountId: Long? = null,
    val date: Long,
    val reason: String?,
    val status: String, // pending/settled
    val settledDate: Long?,
    val amountSettled: Double?
)
