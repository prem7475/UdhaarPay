package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_accounts")
data class BankAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val accountNumber: String,
    val ifscCode: String,
    val bankName: String,
    val accountHolderName: String,
    
    // Changed Enum to String to fix Room error
    val accountType: String = "SAVINGS", 
    
    val isPrimary: Boolean = false,
    val isVerified: Boolean = false,
    val balance: Double = 0.0,
    val showBalance: Boolean = true,
    
    // Changed Date -> Long (Timestamp) to fix Room error
    val lastSyncedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    
    val isActive: Boolean = true
) {
    fun getDisplayName(): String = "$bankName - ${accountNumber.takeLast(4)}"
    fun getMaskedNumber(): String = "••••...${accountNumber.takeLast(4)}"
}