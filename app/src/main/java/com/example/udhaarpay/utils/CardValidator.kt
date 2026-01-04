package com.example.udhaarpay.utils

// Removed: import com.example.udhaarpay.data.model.CardType (This class no longer exists)

object CardValidator {

    /**
     * Validates if a card number is a RuPay card
     * RuPay cards start with 508, 509, 510, 65, 66, etc.
     */
    fun isValidRuPayCard(cardNumber: String): Boolean {
        val sanitized = cardNumber.replace("\\s".toRegex(), "")

        // RuPay BINs (Bank Identification Numbers)
        val ruPayBins = listOf(
            "508", "509", "510", "518", "520", "521", "522", "523", "524",
            "526", "65", "66", "67"
        )

        return ruPayBins.any { sanitized.startsWith(it) } &&
                isValidLuhnNumber(sanitized)
    }

    /**
     * Identifies card type from card number
     * UPDATED: Returns String instead of Enum to match Database Entity
     */
    fun getCardType(cardNumber: String): String {
        val sanitized = cardNumber.replace("\\s".toRegex(), "")

        return when {
            sanitized.startsWith("4") -> "VISA"
            sanitized.startsWith("5") -> "MASTERCARD"
            sanitized.startsWith("3") -> "AMEX"
            isValidRuPayCard(cardNumber) -> "RUPAY"
            else -> "RUPAY" // Default
        }
    }

    /**
     * Validates card number using Luhn algorithm
     */
    fun isValidLuhnNumber(cardNumber: String): Boolean {
        val sanitized = cardNumber.replace("\\s".toRegex(), "")

        if (sanitized.length < 13 || sanitized.length > 19) {
            return false
        }

        if (!sanitized.all { it.isDigit() }) {
            return false
        }

        var sum = 0
        var isEven = false

        for (i in sanitized.length - 1 downTo 0) {
            var digit = Character.getNumericValue(sanitized[i])

            if (isEven) {
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }

            sum += digit
            isEven = !isEven
        }

        return sum % 10 == 0
    }

    /**
     * Validates expiry date (MM/YY format)
     */
    fun isValidExpiry(month: Int, year: Int): Boolean {
        val now = java.util.Calendar.getInstance()
        val currentYear = now.get(java.util.Calendar.YEAR)
        val currentMonth = now.get(java.util.Calendar.MONTH) + 1

        // Year should be 2 or 4 digit
        val fullYear = if (year < 100) 2000 + year else year

        return when {
            fullYear < currentYear -> false
            fullYear == currentYear -> month >= currentMonth
            else -> month in 1..12
        }
    }

    /**
     * Validates CVV (3 or 4 digits)
     */
    fun isValidCVV(cvv: String): Boolean {
        return cvv.matches(Regex("^\\d{3,4}$"))
    }

    /**
     * Masks card number (shows only last 4 digits)
     */
    fun maskCardNumber(cardNumber: String): String {
        val sanitized = cardNumber.replace("\\s".toRegex(), "")
        if (sanitized.length < 4) return cardNumber
        return "•••• •••• •••• " + sanitized.takeLast(4)
    }

    /**
     * Masks CVV
     */
    fun maskCVV(cvv: String): String {
        return if (cvv.length >= 3) "•••" else cvv
    }
}