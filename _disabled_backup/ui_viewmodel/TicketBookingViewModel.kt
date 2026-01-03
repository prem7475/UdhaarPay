package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.TicketBookingDao
import com.example.udhaarpay.data.model.TicketBooking
import com.example.udhaarpay.data.model.TicketCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketBookingViewModel @Inject constructor(
    private val ticketDao: TicketBookingDao
) : ViewModel() {

    private val _bookings = MutableStateFlow<List<TicketBooking>>(emptyList())
    val bookings: StateFlow<List<TicketBooking>> = _bookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var userId: Long = 0

    fun setUserId(userId: Long) {
        this.userId = userId
        loadBookings()
    }

    fun loadBookings() {
        viewModelScope.launch {
            ticketDao.getUserBookings(userId).collect { bookings ->
                _bookings.value = bookings
            }
        }
    }

    fun saveBookingRecord(
        category: TicketCategory,
        platform: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val booking = TicketBooking(
                    userId = userId,
                    category = category,
                    platform = platform
                )

                ticketDao.insertBooking(booking)
                loadBookings()
            } catch (e: Exception) {
                // Log error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
