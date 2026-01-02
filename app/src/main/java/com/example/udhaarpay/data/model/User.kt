package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val id: String,
    val phoneNumber: String,
    val email: String? = null,
    val name: String,
    val profileImageUrl: String? = null,
    val upiId: String,
    val isKycVerified: Boolean = false,
    val walletBalance: Double = 0.0,
    val createdAt: Date = Date(),
    val lastLoginAt: Date = Date(),
    val isActive: Boolean = true
)
