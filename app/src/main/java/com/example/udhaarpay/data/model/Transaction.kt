package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val transactionId: String, // Unique transaction ID
    val type: TransactionType,
    val subType: TransactionSubType? = null,
    val description: String,
    val amount: Double,
    val fee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val timestamp: Date = Date(),
    val status: TransactionStatus = TransactionStatus.PENDING,
    val senderId: String? = null,
    val senderName: String? = null,
    val senderUpiId: String? = null,
    val receiverId: String? = null,
    val receiverName: String? = null,
    val receiverUpiId: String? = null,
    val bankAccountId: Long? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.WALLET,
    val category: String,
    val merchantName: String? = null,
    val merchantCategory: String? = null,
    val location: String? = null,
    val notes: String? = null,
    val referenceNumber: String? = null,
    val isRecurring: Boolean = false,
    val recurringId: String? = null,
    val isDebit: Boolean = false,
    val balanceAfter: Double? = null,
    val metadata: String? = null, // JSON string for additional data
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    val displayAmount: String
        get() = if (isDebit) "-₹${String.format("%.2f", amount)}" else "+₹${String.format("%.2f", amount)}"
    
    val isSuccessful: Boolean
        get() = status == TransactionStatus.SUCCESS || status == TransactionStatus.COMPLETED
    
    val isPending: Boolean
        get() = status == TransactionStatus.PENDING || status == TransactionStatus.PROCESSING
    
    val isFailed: Boolean
        get() = status == TransactionStatus.FAILED || status == TransactionStatus.CANCELLED || status == TransactionStatus.REJECTED
}
