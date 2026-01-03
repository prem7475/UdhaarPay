package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "credit_cards")
data class CreditCard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val cardNumber: String, // Masked, only last 4 visible
    val cardNumberFull: String? = null, // Store full number encrypted in production
    val cardholderName: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val cvv: String? = null,

    // FIX 1: Changed Enum to String to fix Room error
    val cardType: String = CardType.RUPAY.name,

    val lastFourDigits: String,
    val issuerBank: String,
    val isDefault: Boolean = false,
    val isActive: Boolean = true,

    // FIX 2: Changed Date to Long (Timestamp) to fix Room error
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val displayNumber: String
        get() = "•••• •••• •••• $lastFourDigits"

    val displayExpiry: String
        get() = String.format("%02d/%02d", expiryMonth, expiryYear % 100)

    val isExpired: Boolean
        get() {
            val now = Calendar.getInstance()
            val currentYear = now.get(Calendar.YEAR)
            val currentMonth = now.get(Calendar.MONTH) + 1

            return if (expiryYear < currentYear) {
                true
            } else if (expiryYear == currentYear) {
                expiryMonth < currentMonth
            } else {
                false
            }
        }
}

enum class CardType {
    RUPAY,
    VISA,
    MASTERCARD,
    AMEX
}