package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val transactionId: String,
    val type: String, // "CREDIT" or "DEBIT"
    val subType: String? = null,
    val description: String,
    val amount: Double,
    val fee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", 
    val senderId: String? = null,
    val senderName: String? = null,
    val senderUpiId: String? = null,
    val receiverId: String? = null,
    val receiverName: String? = null,
    val receiverUpiId: String? = null,
    val bankAccountId: Long? = null,
    val paymentMethod: String = "WALLET",
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
    val metadata: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val displayAmount: String
        get() = if (isDebit || type == "DEBIT") "-₹%.2f".format(Locale.US, amount) else "+₹%.2f".format(Locale.US, amount)
    
    val isSuccessful: Boolean
        get() = status == "SUCCESS" || status == "COMPLETED"
}