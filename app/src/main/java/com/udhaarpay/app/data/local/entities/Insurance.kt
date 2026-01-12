package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insurances")
data class Insurance(
    @PrimaryKey(autoGenerate = true) val policyId: Long = 0L,
    val policyType: String,
    val provider: String,
    val premium: Double,
    val startDate: Long,
    val expiryDate: Long,
    val status: String,
    val coverage: String
)
