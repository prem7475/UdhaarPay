package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_accounts")
data class BankAccount(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var userId: String = "",
    var accountNumber: String = "",
    var ifscCode: String = "",
    var bankName: String = "",
    var accountHolderName: String = "",

    var accountType: String = "SAVINGS",

    var isPrimary: Boolean = false,
    var isVerified: Boolean = false,
    var balance: Double = 0.0,
    var showBalance: Boolean = true,

    var lastSyncedAt: Long = System.currentTimeMillis(),
    var createdAt: Long = System.currentTimeMillis(),

    var isActive: Boolean = true
) {
    fun getDisplayName(): String = "$bankName - ${accountNumber.takeLast(4)}"
    fun getMaskedNumber(): String = "••••...${accountNumber.takeLast(4)}"
}
