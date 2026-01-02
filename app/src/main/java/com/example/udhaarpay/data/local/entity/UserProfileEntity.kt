package com.example.udhaarpay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val uid: Int = 1, // Single user app, fixed ID
    val fullName: String,
    val phoneNumber: String,
    val email: String,
    val profileImageUri: String?
)
