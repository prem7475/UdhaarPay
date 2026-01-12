package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upi_payments")
data class UPIPayment(
    @PrimaryKey(autoGenerate = true) val transactionId: Long = 0L,
    val senderUPI: String,
    val recipientUPI: String,
    val amount: Double,
    val date: Long,
    val message: String?,
    val status: String,
    val type: String // sent/request
)
