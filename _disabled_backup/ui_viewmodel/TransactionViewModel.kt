package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.domain.usecase.GetAllTransactionsUseCase
import com.example.udhaarpay.data.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Date
import java.util.Calendar

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedSource = MutableStateFlow<String?>(null)
    val selectedSource: StateFlow<String?> = _selectedSource.asStateFlow()

    private val _dateRangeStart = MutableStateFlow<Long?>(null)
    val dateRangeStart: StateFlow<Long?> = _dateRangeStart.asStateFlow()

    private val _dateRangeEnd = MutableStateFlow<Long?>(null)
    val dateRangeEnd: StateFlow<Long?> = _dateRangeEnd.asStateFlow()

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
                        applyFilters()
                        _isLoading.value = false
                        _error.value = null
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load transactions"
                _isLoading.value = false
            }
        }
    }

    private fun applyFilters() {
        var filtered = _transactions.value

        // Filter by category
        _selectedCategory.value?.let { category ->
            if (category.isNotEmpty()) {
                filtered = filtered.filter { it.category.equals(category, ignoreCase = true) }
            }
        }

        // Filter by source (Bank/Wallet via paymentMethod)
        _selectedSource.value?.let { source ->
            if (source.isNotEmpty()) {
                filtered = filtered.filter { it.paymentMethod.name.equals(source, ignoreCase = true) }
            }
        }

        // Filter by date range
        _dateRangeStart.value?.let { startTime ->
            filtered = filtered.filter { it.timestamp.time >= startTime }
        }
        _dateRangeEnd.value?.let { endTime ->
            val endOfDay = endTime + (24 * 60 * 60 * 1000) // Add 24 hours
            filtered = filtered.filter { it.timestamp.time <= endOfDay }
        }

        _filteredTransactions.value = filtered
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
        applyFilters()
    }

    fun setSource(source: String?) {
        _selectedSource.value = source
        applyFilters()
    }

    fun setDateRange(startDate: Long?, endDate: Long?) {
        _dateRangeStart.value = startDate
        _dateRangeEnd.value = endDate
        applyFilters()
    }

    fun clearFilters() {
        _selectedCategory.value = null
        _selectedSource.value = null
        _dateRangeStart.value = null
        _dateRangeEnd.value = null
        applyFilters()
    }

    fun getAvailableCategories(): List<String> {
        return _transactions.value
            .map { it.category }
            .distinct()
            .sorted()
    }

    fun getAvailableSources(): List<String> {
        return _transactions.value
            .map { it.paymentMethod.name }
            .distinct()
            .sorted()
    }

    fun refreshTransactions() {
        loadTransactions()
    }
}
