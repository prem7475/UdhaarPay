package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.domain.usecase.GetAllTransactionsUseCase
import com.example.udhaarpay.data.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getAllTransactionsUseCase()
                    .catch { exception ->
                        _error.value = exception.message ?: "Unknown error occurred"
                        _isLoading.value = false
                    }
                    .collect { transactionList ->
                        _transactions.value = transactionList
                        _isLoading.value = false
                        _error.value = null
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load transactions"
                _isLoading.value = false
            }
        }
    }

    fun refreshTransactions() {
        loadTransactions()
    }
}
