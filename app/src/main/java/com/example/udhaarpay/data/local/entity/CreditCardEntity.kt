package com.example.udhaarpay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credit_cards")
data class CreditCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val number: String,
    val expiry: String,
    val cvv: String,
    val limit: Double,
    val balanceUsed: Double,
    val isRupay: Boolean
)
