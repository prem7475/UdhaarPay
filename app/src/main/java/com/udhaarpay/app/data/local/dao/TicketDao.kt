package com.udhaarpay.app.data.local.dao

import androidx.room.*
import com.udhaarpay.app.data.local.entities.Ticket
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ticket: Ticket): Long

    @Delete
    suspend fun delete(ticket: Ticket): Int

    @Query("SELECT * FROM tickets")
    fun getAll(): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets WHERE ticketType = :type")
    fun getByType(type: String): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets WHERE date = :date")
    fun getByDate(date: Long): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets WHERE status = :status")
    fun getByStatus(status: String): Flow<List<Ticket>>
}
