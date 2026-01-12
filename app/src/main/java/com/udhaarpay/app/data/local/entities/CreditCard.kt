package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credit_cards")
data class CreditCard(
    @PrimaryKey(autoGenerate = true) val cardId: Long = 0L,
    val cardNumber: String, // last 4
    val cardType: String, // RuPay/Visa/Mastercard
    val issuer: String,
    val balance: Double,
    val limit: Double,
    val expiry: String,
    val status: String,
    val upiLinked: Boolean
)
