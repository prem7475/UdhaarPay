package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.udhaarpay.data.model.TicketBooking
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketBookingDao {
    @Insert
    suspend fun insertBooking(booking: TicketBooking): Long

    @Query("SELECT * FROM tickets WHERE userId = :userId ORDER BY bookingDate DESC")
    fun getUserBookings(userId: Long): Flow<List<TicketBooking>>

    @Query("SELECT * FROM tickets WHERE id = :bookingId")
    suspend fun getBookingById(bookingId: Long): TicketBooking?

    @Query("SELECT COUNT(*) FROM tickets WHERE userId = :userId")
    suspend fun getBookingCount(userId: Long): Int
}
