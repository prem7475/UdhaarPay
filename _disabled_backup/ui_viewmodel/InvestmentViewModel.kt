package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.InvestmentDao
import com.example.udhaarpay.data.model.Investment
import com.example.udhaarpay.data.model.InvestmentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvestmentViewModel @Inject constructor(
    private val investmentDao: InvestmentDao
) : ViewModel() {

    private val _investments = MutableStateFlow<List<Investment>>(emptyList())
    val investments: StateFlow<List<Investment>> = _investments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var userId: Long = 0

    fun setUserId(userId: Long) {
        this.userId = userId
        loadInvestments()
    }

    fun loadInvestments() {
        viewModelScope.launch {
            investmentDao.getUserInvestments(userId).collect { investments ->
                _investments.value = investments
            }
        }
    }

    fun saveInvestmentRecord(
        type: InvestmentType,
        broker: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val investment = Investment(
                    userId = userId,
                    type = type,
                    broker = broker
                )

                investmentDao.insertInvestment(investment)
                loadInvestments()
            } catch (e: Exception) {
                // Log error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
