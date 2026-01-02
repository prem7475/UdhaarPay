package com.example.udhaarpay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bankName: String,
    val accountNumber: String, // Stored as full, masked in UI
    val ifscCode: String,
    val balance: Double,
    val type: String, // Savings/Current
    val isPrimary: Boolean = false
)
