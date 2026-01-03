package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val transactionId: String, // Unique transaction ID
    
    // Changed Enums to String to fix Room errors
    val type: String, // "CREDIT" or "DEBIT"
    val subType: String? = null,
    
    val description: String,
    val amount: Double,
    val fee: Double = 0.0,
    val totalAmount: Double = 0.0,
    
    // Changed Date -> Long (Timestamp)
    val timestamp: Long = System.currentTimeMillis(),
    
    // Changed Enum -> String (Default: "PENDING")
    val status: String = "PENDING", 
    
    val senderId: String? = null,
    val senderName: String? = null,
    val senderUpiId: String? = null,
    val receiverId: String? = null,
    val receiverName: String? = null,
    val receiverUpiId: String? = null,
    val bankAccountId: Long? = null,
    
    // Changed Enum -> String (Default: "WALLET")
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
    val metadata: String? = null, // JSON string for additional data
    
    // Changed Date -> Long
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val displayAmount: String
        get() = if (isDebit) "-₹${String.format("%.2f", amount)}" else "+₹${String.format("%.2f", amount)}"
    
    val isSuccessful: Boolean
        get() = status == "SUCCESS" || status == "COMPLETED"
    
    val isPending: Boolean
        get() = status == "PENDING" || status == "PROCESSING"
    
    val isFailed: Boolean
        get() = status == "FAILED" || status == "CANCELLED" || status == "REJECTED"
}