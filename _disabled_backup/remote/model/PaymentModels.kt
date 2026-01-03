package com.example.udhaarpay.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Send money request
 */
data class SendMoneyRequest(
    @SerializedName("recipientName")
    val recipientName: String,
    @SerializedName("upiId")
    val upiId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("description")
    val description: String? = null
)

/**
 * Pay bill request
 */
data class PayBillRequest(
    @SerializedName("billType")
    val billType: String,
    @SerializedName("billNumber")
    val billNumber: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("dueDate")
    val dueDate: String? = null
)

/**
 * Scan & pay request
 */
data class ScanPayRequest(
    @SerializedName("payeeName")
    val payeeName: String,
    @SerializedName("qrCode")
    val qrCode: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("description")
    val description: String? = null
)

/**
 * Add money request
 */
data class AddMoneyRequest(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("paymentMethod")
    val paymentMethod: String,
    @SerializedName("bankAccountId")
    val bankAccountId: String? = null
)

/**
 * Transaction response
 */
data class TransactionResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("description")
    val description: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("recipient")
    val recipient: String? = null,
    @SerializedName("upiId")
    val upiId: String? = null,
    @SerializedName("billType")
    val billType: String? = null,
    @SerializedName("billNumber")
    val billNumber: String? = null
)

/**
 * Wallet balance response
 */
data class WalletBalanceResponse(
    @SerializedName("balance")
    val balance: Double,
    @SerializedName("currency")
    val currency: String = "INR",
    @SerializedName("lastUpdated")
    val lastUpdated: String
)

/**
 * Payment confirmation response
 */
data class PaymentConfirmationResponse(
    @SerializedName("transactionId")
    val transactionId: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("timestamp")
    val timestamp: String
)
