package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class TransferType {
    BANK_TO_BANK,
    BANK_TO_WALLET,
    WALLET_TO_BANK
}

@Entity(tableName = "transfers")
data class Transfer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val fromAccount: String, // "Bank" or "Wallet"
    val toAccount: String,   // "Bank" or "Wallet"
    val amount: Double,
    val transferType: TransferType,
    val status: TransactionStatus = TransactionStatus.SUCCESS,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String = ""
)
