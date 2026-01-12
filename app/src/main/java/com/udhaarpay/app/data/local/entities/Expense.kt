package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val expenseId: Long = 0L,
    val amount: Double,
    val category: String,
    val subCategory: String?,
    val account: String,
    val accountName: String,
    val description: String?,
    val date: Long,
    val month: String,
    val receiptUrl: String?
)
