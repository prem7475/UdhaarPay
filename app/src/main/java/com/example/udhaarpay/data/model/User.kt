package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Note: tableName is set to "user_profile" to match your DAO queries.
// If you prefer "users", you must also update your UserDao.kt query to "SELECT * FROM users..."
@Entity(tableName = "user_profile")
data class User(
    @PrimaryKey
    val userId: String,
    
    val phoneNumber: String,
    val email: String? = null,
    val name: String,
    val profileImageUrl: String? = null,
    val upiId: String,
    val isKycVerified: Boolean = false,
    
    val walletBalance: Double = 5000.0,
    val bankBalance: Double = 10000.0,
    
    // Using Long (Timestamp) for Room compatibility
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    
    val isActive: Boolean = true
)