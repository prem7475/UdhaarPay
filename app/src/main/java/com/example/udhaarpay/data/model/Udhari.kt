package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Changed tableName to "udhari_records" to fix the [SQLITE_ERROR]
@Entity(tableName = "udhari_records")
data class Udhari(
    @PrimaryKey(autoGenerate = true) 
    val id: Long = 0,
    
    // Linked to the User
    val userId: String, 
    
    val customerName: String,
    val phoneNumber: String? = null,
    val amount: Double,
    
    // "GIVEN" (You gave money) or "TAKEN" (You took money)
    val type: String = "GIVEN", 
    
    val isPaid: Boolean = false,
    
    // Timestamps (Long)
    val dueDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)