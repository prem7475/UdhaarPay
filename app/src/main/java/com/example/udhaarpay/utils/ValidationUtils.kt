package com.example.udhaarpay.utils

object ValidationUtils {

    fun isValidAmount(amount: String): Boolean {
        return try {
            val value = amount.toDouble()
            value > 0 && value <= Constants.MAX_TRANSACTION_AMOUNT
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isValidUpiId(upiId: String): Boolean {
        val upiPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+$".toRegex()
        return upiPattern.matches(upiId)
    }

    fun isValidUpiPin(pin: String): Boolean {
        return pin.length in Constants.MIN_UPI_PIN_LENGTH..Constants.MAX_UPI_PIN_LENGTH &&
               pin.all { it.isDigit() }
    }

    fun isValidPhoneNumber(phone: String): Boolean {
        val phonePattern = "^[6-9]\\d{9}$".toRegex()
        return phonePattern.matches(phone)
    }

    fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return emailPattern.matches(email)
    }

    fun sanitizeAmount(amount: String): String {
        return amount.replace("[^\\d.]".toRegex(), "")
    }

    fun formatAmount(amount: Double): String {
        return String.format("₹ %.2f", amount)
    }

    fun formatAmount(amount: String): String {
        return try {
            formatAmount(amount.toDouble())
        } catch (e: NumberFormatException) {
            "₹ 0.00"
        }
    }
}
