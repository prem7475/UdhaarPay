package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investments")
data class Investment(
    @PrimaryKey(autoGenerate = true) val investmentId: Long = 0L,
    val brokerName: String,
    val fundName: String?,
    val type: String, // sip/mutual/demat/bond
    val amount: Double,
    val frequency: String?, // monthly/quarterly
    val date: Long,
    val currentValue: Double?,
    val returns: Double?
)
