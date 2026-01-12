package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.data.local.dao.TicketDao
import com.udhaarpay.app.data.local.entities.Ticket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val ticketDao: TicketDao
) : ViewModel() {
    val tickets: StateFlow<List<Ticket>> =
        ticketDao.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(ticket: Ticket) {
        viewModelScope.launch { ticketDao.insert(ticket) }
    }

    fun delete(ticket: Ticket) {
        viewModelScope.launch { ticketDao.delete(ticket) }
    }
}
