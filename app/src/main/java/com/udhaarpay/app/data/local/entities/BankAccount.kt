package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_accounts")
data class BankAccount(
    @PrimaryKey(autoGenerate = true) val accountId: Long = 0L,
    val bankName: String,
    val accountNumber: String,
    val ifscCode: String,
    val accountType: String, // Savings/Current/Salary
    val balance: Double,
    val nickname: String?,
    val addedDate: Long
)
