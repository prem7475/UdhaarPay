package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rupay_cards")
data class RuPayCard(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val cardNumber: String, // Full card number
    val cardHolderName: String,
    val expiryDate: String, // MM/YY format
    val cvv: String,
    val cardType: RuPayCardType = RuPayCardType.STANDARD,
    val availableBalance: Double = 0.0,
    val creditLimit: Double = 0.0,
    val isDefault: Boolean = false,
    val isActive: Boolean = true,
    val addedDate: Long = System.currentTimeMillis(),
    val last4Digits: String = "" // Last 4 digits of card
) {
    init {
        require(isValidRuPayCard(cardNumber)) { "Invalid RuPay card number" }
    }

    fun getDisplayName(): String = "$cardHolderName - ${last4Digits}"
    fun getMaskedNumber(): String = "**** **** **** ${last4Digits}"

    companion object {
        /**
         * Validate RuPay card number using Luhn algorithm
         * RuPay cards start with 508 or 606-607-608
         */
        fun isValidRuPayCard(cardNumber: String): Boolean {
            val cleaned = cardNumber.replace(" ", "").replace("-", "")
            if (cleaned.length != 16) return false

            // RuPay card prefixes
            val isRuPayPrefix = cleaned.startsWith("508") ||
                    cleaned.startsWith("606") ||
                    cleaned.startsWith("607") ||
                    cleaned.startsWith("608")

            if (!isRuPayPrefix) return false

            // Luhn algorithm validation
            return isValidLuhn(cleaned)
        }

        private fun isValidLuhn(cardNumber: String): Boolean {
            var sum = 0
            var isSecond = false

            for (i in cardNumber.length - 1 downTo 0) {
                var digit = cardNumber[i].toString().toInt()

                if (isSecond) {
                    digit *= 2
                    if (digit > 9) {
                        digit -= 9
                    }
                }

                sum += digit
                isSecond = !isSecond
            }

            return sum % 10 == 0
        }
    }
}

enum class RuPayCardType {
    STANDARD,
    CREDIT,
    PREPAID,
    DEBIT
}
