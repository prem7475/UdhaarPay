package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.BillPaymentDao
import com.example.udhaarpay.data.model.BillPayment
import com.example.udhaarpay.data.model.BillCategory
import com.example.udhaarpay.data.model.TransactionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillPaymentViewModel @Inject constructor(
    private val billDao: BillPaymentDao
) : ViewModel() {

    private val _bills = MutableStateFlow<List<BillPayment>>(emptyList())
    val bills: StateFlow<List<BillPayment>> = _bills.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    private var userId: Long = 0

    fun setUserId(userId: Long) {
        this.userId = userId
        loadBills()
    }

    fun loadBills() {
        viewModelScope.launch {
            billDao.getUserBills(userId).collect { bills ->
                _bills.value = bills
            }
        }
    }

    fun saveBillRecord(
        category: BillCategory,
        provider: String,
        amount: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val bill = BillPayment(
                    userId = userId,
                    category = category,
                    provider = provider,
                    amount = amount,
                    status = TransactionStatus.SUCCESS
                )

                billDao.insertBill(bill)
                _success.value = "Bill payment saved for $provider"
                _error.value = null
                loadBills()
            } catch (e: Exception) {
                _error.value = "Failed to save bill: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _success.value = null
    }
}
