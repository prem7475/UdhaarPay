package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val personName: String,
    val phoneNumber: String = "",
    val email: String = "",
    val amount: Double,
    val debtType: DebtType, // LENT_TO or BORROWED_FROM
    val category: DebtCategory, // PERSONAL, LOAN, SALARY, etc.
    val fromAccountId: Int? = null, // Which account this debt is from
    val description: String = "",
    val dueDate: Long? = null,
    val isSettled: Boolean = false,
    val settledAmount: Double = 0.0,
    val remainingAmount: Double = amount,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

enum class DebtType {
    LENT_TO,      // Money I gave to someone
    BORROWED_FROM // Money I borrowed from someone
}

enum class DebtCategory {
    PERSONAL,
    LOAN,
    SALARY,
    BONUS,
    INVESTMENT,
    TRANSPORTATION,
    FOOD,
    TRAVEL,
    SHOPPING,
    UTILITIES,
    RENT,
    OTHER
}

data class DebtSummary(
    val totalLent: Double = 0.0,
    val totalBorrowed: Double = 0.0,
    val netAmount: Double = 0.0, // Positive = owed to me, Negative = I owe
    val pendingLent: Double = 0.0,
    val pendingBorrowed: Double = 0.0
)
