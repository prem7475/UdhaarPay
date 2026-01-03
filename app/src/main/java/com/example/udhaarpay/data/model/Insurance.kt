package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class InsuranceCategory {
    HEALTH, LIFE, VEHICLE, TRAVEL
}

data class InsuranceProvider(
    val name: String,
    val website: String,
    val category: InsuranceCategory,
    val emoji: String
)

@Entity(tableName = "insurance")
data class Insurance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val category: InsuranceCategory,
    val provider: String,
    val visitDate: Long = System.currentTimeMillis()
)

val insuranceProviders = listOf(
    InsuranceProvider("PolicyBazaar", "https://www.policybazaar.com", InsuranceCategory.HEALTH, "🏥"),
    InsuranceProvider("Acko", "https://www.acko.com", InsuranceCategory.VEHICLE, "🚗"),
    InsuranceProvider("LIC", "https://www.licindia.in", InsuranceCategory.LIFE, "🤝"),
    InsuranceProvider("HDFC Life", "https://www.hdfclife.com", InsuranceCategory.LIFE, "❤️"),
    InsuranceProvider("PolicyBazaar", "https://www.policybazaar.com", InsuranceCategory.TRAVEL, "✈️"),
    InsuranceProvider("Acko", "https://www.acko.com", InsuranceCategory.HEALTH, "🏥")
)
