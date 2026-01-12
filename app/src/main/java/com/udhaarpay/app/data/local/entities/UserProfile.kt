package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val userId: String,
    // Removed id, userId is now the PK
    val fullName: String,
    val email: String,
    val phone: String,
    val dateOfBirth: Long,
    val gender: String,
    val address: String,
    val city: String,
    val state: String,
    val pincode: String,
    val profilePhotoUrl: String?,
    val panNumber: String?,
    val aadhaarNumber: String?,
    val kycStatus: Boolean,
    val kycDate: Long?
)
