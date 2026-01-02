package com.example.udhaarpay.data.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val upiId: String,
    val hasUpiApp: Boolean = false,
    val isFrequent: Boolean = false,
    val lastTransactionDate: Long? = null,
    val transactionCount: Int = 0,
    val avatarUrl: String? = null
)
