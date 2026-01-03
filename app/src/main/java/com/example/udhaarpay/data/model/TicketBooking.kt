package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TicketCategory {
    MOVIES, FLIGHTS, TRAINS, BUS
}

data class TicketOption(
    val category: TicketCategory,
    val name: String,
    val website: String,
    val emoji: String
)

@Entity(tableName = "tickets")
data class TicketBooking(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val category: TicketCategory,
    val platform: String,
    val bookingDate: Long = System.currentTimeMillis()
)

val ticketOptions = listOf(
    TicketOption(TicketCategory.MOVIES, "BookMyShow", "https://www.bookmyshow.com", "🎬"),
    TicketOption(TicketCategory.FLIGHTS, "Skyscanner", "https://www.skyscanner.com", "✈️"),
    TicketOption(TicketCategory.TRAINS, "IRCTC", "https://www.irctc.co.in", "🚂"),
    TicketOption(TicketCategory.BUS, "RedBus", "https://www.redbus.in", "🚌")
)
