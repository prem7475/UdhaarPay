package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "bank_accounts")
data class BankAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val accountNumber: String,
    val ifscCode: String,
    val bankName: String,
    val accountHolderName: String,
    val accountType: AccountType = AccountType.SAVINGS,
    val isPrimary: Boolean = false,
    val isVerified: Boolean = false,
    val balance: Double = 0.0,
    val showBalance: Boolean = true, // Added missing field
    val lastSyncedAt: Date = Date(),
    val createdAt: Date = Date(),
    val isActive: Boolean = true
)

enum class AccountType {
    SAVINGS,
    CURRENT,
    SALARY,
    NRI
}
