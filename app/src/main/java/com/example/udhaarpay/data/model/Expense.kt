package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) 
    val id: Long = 0,
    
    // Added userId so you can filter expenses per user
    val userId: String = "", 
    
    val title: String,
    val amount: Double,
    val date: Long,

    // These fields were missing and caused the build crash
    val category: String = "General",
    val description: String = "",
    val source: String = "Manual" // e.g., "SMS", "Manual"
)