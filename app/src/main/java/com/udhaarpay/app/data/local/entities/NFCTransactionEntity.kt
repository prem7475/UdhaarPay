package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nfc_transactions")
data class NFCTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val transactionRef: String,
    val cardId: Long,
    val cardLast4: String,
    val amount: Double,
    val merchant: String,
    val timestamp: Long,
    val status: String,
    val rewardEarned: Double
)
