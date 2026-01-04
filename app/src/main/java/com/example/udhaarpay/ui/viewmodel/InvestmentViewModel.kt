package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.model.InvestmentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvestmentViewModel @Inject constructor() : ViewModel() {
    private val _investments = MutableStateFlow<List<String>>(emptyList())
    val investments: StateFlow<List<String>> = _investments

    fun addInvestment(userId: Long, type: InvestmentType, broker: String) {
        viewModelScope.launch {
            // Simulate adding investment
            val new = _investments.value + "${type.name} at $broker"
            _investments.value = new
        }
    }
}