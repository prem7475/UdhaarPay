package com.udhaarpay.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credit_cards")
data class CreditCard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val cardNumber: String,       // Full number
    val cardHolderName: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val cvv: String,
    val issuerBank: String,       // e.g. HDFC, SBI
    val cardType: String,         // VISA, RUPAY, MASTERCARD
    val cardColor: String = "#1E3A8A", // Default Blue
    // Financial details
    val limit: Double = 50000.0,
    val balanceUsed: Double = 0.0,
    val isDefault: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Helper property for UI (Not stored in DB)
    val lastFourDigits: String
        get() = if (cardNumber.length >= 4) cardNumber.takeLast(4) else cardNumber
    val expiry: String
        get() = "$expiryMonth/${'$'}{expiryYear % 100}"
}
