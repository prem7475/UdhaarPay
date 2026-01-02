package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

// Card Model
@Entity(tableName = "cards")
data class BankCard(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val bankName: String,
    val cardNumber: String,
    val cardType: String, // VISA, Mastercard, RuPay
    val cardholderName: String,
    val expiryMonth: String,
    val expiryYear: String,
    val cvv: String,
    val bankLogo: String = "",
    val balance: Double = 0.0,
    val isPrimary: Boolean = false,
    val isNFCEnabled: Boolean = false,
    val lastFourDigits: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Serializable

// Offer Model
@Entity(tableName = "offers")
data class Offer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val discount: String, // "50% OFF", "₹500 OFF"
    val category: String, // All, Food, Travel, Electronics, etc.
    val imageUrl: String = "",
    val code: String = "",
    val validFrom: Long,
    val validTill: Long,
    val minAmount: Double = 0.0,
    val maxDiscount: Double = 0.0
) : Serializable

// Service Model
@Entity(tableName = "services")
data class Service(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serviceName: String,
    val category: String, // Recharge, Bills, Travel, etc.
    val icon: String = "",
    val description: String = "",
    val isActive: Boolean = true
) : Serializable

// Transaction Category Model
@Entity(tableName = "transaction_categories")
data class TransactionCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val categoryName: String, // Food, Transport, etc.
    val amount: Double,
    val transactionCount: Int = 1,
    val percentage: Float = 0f
) : Serializable

// Spending Analytics Model
@Entity(tableName = "spending_analytics")
data class SpendingAnalytics(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val month: String, // "2024-01"
    val totalSpent: Double = 0.0,
    val totalReceived: Double = 0.0,
    val weeklyData: String = "", // JSON string of weekly spending
    val monthlyData: String = "", // JSON string of monthly spending
    val categoryBreakdown: String = "" // JSON string of category-wise spending
) : Serializable

// QR Scan Model
@Entity(tableName = "qr_scans")
data class QRScan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val upiId: String,
    val recipientName: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isProcessed: Boolean = false
) : Serializable

// Notification Model
@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val title: String,
    val message: String,
    val type: String = "INFO", // INFO, SUCCESS, ERROR, ALERT
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionUrl: String = ""
) : Serializable
