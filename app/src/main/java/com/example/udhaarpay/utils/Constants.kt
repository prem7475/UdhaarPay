package com.example.udhaarpay.utils

object Constants {

    // API
    const val BASE_URL = "https://api.udhaarpay.com/"
    const val TIMEOUT_SECONDS = 30L

    // Preferences
    const val PREFS_NAME = "udhaar_pay_prefs"
    const val KEY_ENCRYPTED_PREFS = "encrypted_udhaar_pay_prefs"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_WALLET_BALANCE = "wallet_balance"
    const val KEY_UPI_PIN = "upi_pin"
    const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"

    // Notification Channels
    const val CHANNEL_PAYMENT = "payment_channel"
    const val CHANNEL_PROMOTIONAL = "promotional_channel"
    const val CHANNEL_SECURITY = "security_channel"

    // Transaction Types
    const val TRANSACTION_TYPE_SEND_MONEY = "send_money"
    const val TRANSACTION_TYPE_RECEIVE_MONEY = "receive_money"
    const val TRANSACTION_TYPE_BILL_PAYMENT = "bill_payment"
    const val TRANSACTION_TYPE_RECHARGE = "recharge"
    const val TRANSACTION_TYPE_BANK_TRANSFER = "bank_transfer"
    const val TRANSACTION_TYPE_PAYMENT_REQUEST = "payment_request"
    const val TRANSACTION_TYPE_SPLIT_BILL = "split_bill"
    const val TRANSACTION_TYPE_GROUP_PAYMENT = "group_payment"

    // Error Messages
    const val ERROR_NETWORK = "Network error. Please check your connection."
    const val ERROR_INVALID_PIN = "Invalid UPI PIN"
    const val ERROR_INSUFFICIENT_BALANCE = "Insufficient wallet balance"
    const val ERROR_PAYMENT_FAILED = "Payment failed. Please try again."
    const val ERROR_INVALID_AMOUNT = "Please enter a valid amount"
    const val ERROR_INVALID_UPI_ID = "Please enter a valid UPI ID"
    const val ERROR_USER_NOT_FOUND = "User not found"
    const val ERROR_TRANSACTION_FAILED = "Transaction failed. Please try again."

    // Limits
    const val MAX_TRANSACTION_AMOUNT = 10000.0
    const val MIN_UPI_PIN_LENGTH = 4
    const val MAX_UPI_PIN_LENGTH = 6
    const val MAX_DAILY_TRANSACTION_LIMIT = 50000.0
    const val MAX_MONTHLY_TRANSACTION_LIMIT = 200000.0

    // Database
    const val DATABASE_NAME = "udhaarpay_database"
    const val DATABASE_VERSION = 1

    // QR Code
    const val QR_CODE_SIZE = 512
    const val UPI_SCHEME = "upi://pay"

    // Analytics
    const val ANALYTICS_PERIOD_DAYS = 30
    const val BUDGET_WARNING_THRESHOLD = 80.0

    // Security
    const val SESSION_TIMEOUT_MINUTES = 30
    const val MAX_LOGIN_ATTEMPTS = 3

    // Cache
    const val CACHE_CONTACTS_TIMEOUT_HOURS = 24
    const val CACHE_TRANSACTIONS_TIMEOUT_MINUTES = 5

    // API Endpoints
    const val ENDPOINT_LOGIN = "auth/login"
    const val ENDPOINT_REGISTER = "auth/register"
    const val ENDPOINT_TRANSACTIONS = "transactions"
    const val ENDPOINT_USERS = "users"
    const val ENDPOINT_BANK_ACCOUNTS = "bank-accounts"
    const val ENDPOINT_BILL_PAYMENTS = "bill-payments"

    // Payment Methods
    const val PAYMENT_METHOD_WALLET = "wallet"
    const val PAYMENT_METHOD_BANK = "bank"
    const val PAYMENT_METHOD_CARD = "card"
    const val PAYMENT_METHOD_UPI = "upi"

    // Bill Types
    const val BILL_TYPE_ELECTRICITY = "electricity"
    const val BILL_TYPE_WATER = "water"
    const val BILL_TYPE_GAS = "gas"
    const val BILL_TYPE_INTERNET = "internet"
    const val BILL_TYPE_MOBILE = "mobile"
    const val BILL_TYPE_DTH = "dth"
    const val BILL_TYPE_BROADBAND = "broadband"

    // Transaction Status
    const val STATUS_PENDING = "pending"
    const val STATUS_PROCESSING = "processing"
    const val STATUS_SUCCESS = "success"
    const val STATUS_FAILED = "failed"
    const val STATUS_CANCELLED = "cancelled"

    // Categories
    val SPENDING_CATEGORIES = listOf(
        "Food & Dining",
        "Transportation",
        "Shopping",
        "Entertainment",
        "Bills & Utilities",
        "Healthcare",
        "Education",
        "Travel",
        "Investment",
        "Others"
    )

    val INCOME_CATEGORIES = listOf(
        "Salary",
        "Freelance",
        "Business",
        "Investment",
        "Gift",
        "Refund",
        "Others"
    )
}
