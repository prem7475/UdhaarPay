package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val userId: String, // e.g., "user_001" or Phone Number
    
    val name: String,
    val phoneNumber: String,
    val email: String? = null,
    val profileImage: String? = null,
    val upiId: String? = null,
    val kycStatus: String = "PENDING", // VERIFIED, PENDING, FAILED
    
    val createdAt: Long = System.currentTimeMillis()
)