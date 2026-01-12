package com.udhaarpay.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = true) val ticketId: Long = 0L,
    val ticketType: String, // movie/flight/train/bus
    val movieName: String?,
    val destination: String?,
    val cinema: String?,
    val provider: String?,
    val date: Long,
    val seats: String?,
    val amount: Double,
    val status: String
)
