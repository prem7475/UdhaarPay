package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_payments")
data class RecurringPayment(
    @PrimaryKey val id: String,
    val amount: Double,
    val recipient: String,
    val interval: String, // e.g., "monthly", "weekly"
    val nextDueDate: Long
)
