package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nfc_transactions")
data class NFCTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val cardId: Int, // Which RuPay card was used
    val merchantName: String,
    val amount: Double,
    val transactionId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: NFCTransactionStatus = NFCTransactionStatus.SUCCESS,
    val location: String? = null
)

enum class NFCTransactionStatus {
    SUCCESS,
    FAILED,
    PENDING,
    DECLINED
}
