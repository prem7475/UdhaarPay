package com.example.udhaarpay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey val id: Int = 1, // Single wallet for the user
    val name: String = "Cash Wallet",
    val currentBalance: Double
)
