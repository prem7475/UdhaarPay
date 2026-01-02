package com.example.udhaarpay.data.model

data class QRCodeData(
    val type: QRCodeType,
    val userId: String? = null,
    val merchantId: String? = null,
    val merchantName: String? = null,
    val payeeName: String? = null,
    val upiId: String? = null,
    val phoneNumber: String? = null,
    val amount: Double? = null,
    val currency: String? = "INR",
    val transactionNote: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class QRCodeType {
    UPI_PAYMENT,
    MERCHANT_PAYMENT,
    CONTACT_SHARE
}
