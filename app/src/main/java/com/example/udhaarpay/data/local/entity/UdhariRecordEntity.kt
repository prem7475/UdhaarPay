package com.example.udhaarpay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "udhari_records")
data class UdhariRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personName: String,
    val amount: Double,
    val type: String, // GIVEN/TAKEN
    val category: String = "Other",
    val paymentSource: String = "Wallet", // Bank Name or Wallet
    val date: Long,
    val isSettled: Boolean
)
