package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.model.SpendingAnalytics
import com.example.udhaarpay.data.model.TransactionCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor() : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _monthlySpending = MutableStateFlow<SpendingAnalytics?>(null)
    val monthlySpending: StateFlow<SpendingAnalytics?> = _monthlySpending

    private val _categoryStats = MutableStateFlow<List<TransactionCategory>>(emptyList())
    val categoryStats: StateFlow<List<TransactionCategory>> = _categoryStats

    private val _selectedMonth = MutableStateFlow("Jan")
    val selectedMonth: StateFlow<String> = _selectedMonth

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Mock data for now
                val mockSpending = SpendingAnalytics(
                    userId = 1,
                    month = "2024-01",
                    totalSpent = 15000.0,
                    totalReceived = 20000.0,
                    weeklyData = "2500,3200,1800,4500,2200,3800,1500",
                    monthlyData = "",
                    categoryBreakdown = ""
                )

                val mockCategories = listOf(
                    TransactionCategory(userId = 1, categoryName = "Food", amount = 4500.0),
                    TransactionCategory(userId = 1, categoryName = "Transport", amount = 3200.0),
                    TransactionCategory(userId = 1, categoryName = "Shopping", amount = 2800.0),
                    TransactionCategory(userId = 1, categoryName = "Bills", amount = 2500.0),
                    TransactionCategory(userId = 1, categoryName = "Entertainment", amount = 2000.0)
                )

                _monthlySpending.value = mockSpending
                _categoryStats.value = mockCategories

            } catch (e: Exception) {
                _error.value = "Failed to load analytics: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectMonth(month: String) {
        _selectedMonth.value = month
        loadAnalytics() // Reload data for selected month
    }

    fun clearError() {
        _error.value = null
    }
}
