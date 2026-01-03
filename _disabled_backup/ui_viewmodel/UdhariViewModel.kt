package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.UdhariDao
import com.example.udhaarpay.data.model.Udhari
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UdhariState(
    val records: List<Udhari> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class UdhariViewModel @Inject constructor(
    private val udhariDao: UdhariDao
) : ViewModel() {

    private val _state = MutableStateFlow(UdhariState())
    val state: StateFlow<UdhariState> = _state.asStateFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            udhariDao.getAllUdhariRecords().collect { records ->
                _state.value = _state.value.copy(records = records)
            }
        }
    }

    fun addRecord(name: String, amount: Double, type: String, category: String = "Other", source: String = "Wallet") {
        viewModelScope.launch {
            val record = Udhari(
                userId = "current_user", // Should come from auth
                customerName = name,
                amount = amount,
                type = type,
                isPaid = false,
                createdAt = System.currentTimeMillis()
            )
            udhariDao.insertUdhariRecord(record)
        }
    }

    fun settleRecord(id: Long) {
        viewModelScope.launch {
            udhariDao.settleUdhari(id)
        }
    }
}
