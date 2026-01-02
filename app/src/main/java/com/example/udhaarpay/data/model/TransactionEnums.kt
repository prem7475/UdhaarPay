package com.example.udhaarpay.data.model

enum class TransactionSubType {
    // Add specific subtypes if needed, e.g., for BILL_PAYMENT
    ELECTRICITY, WATER, GAS, MOBILE
}

enum class TransactionStatus {
    PENDING, PROCESSING, COMPLETED, SUCCESS, FAILED, CANCELLED, REJECTED
}

enum class PaymentMethod {
    WALLET, UPI, DEBIT_CARD, CREDIT_CARD, NET_BANKING
}
