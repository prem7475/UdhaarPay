package com.udhaarpay.app.repository

import com.udhaarpay.app.data.local.dao.TicketDao
import com.udhaarpay.app.data.local.entities.Ticket
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketRepository @Inject constructor(
    private val ticketDao: TicketDao
) {
    fun getAll(): Flow<List<Ticket>> = ticketDao.getAll()
    suspend fun insert(ticket: Ticket): Long = ticketDao.insert(ticket)
    suspend fun delete(ticket: Ticket): Int = ticketDao.delete(ticket)
}
