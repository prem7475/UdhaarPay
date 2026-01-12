package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.repository.InvestmentRepository
import com.udhaarpay.app.data.local.entities.Investment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvestmentViewModel @Inject constructor(
    private val repository: InvestmentRepository
) : ViewModel() {
    val investments: StateFlow<List<Investment>> =
        repository.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val summary: StateFlow<Double?> =
        repository.getSummary().stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun insert(investment: Investment) {
        viewModelScope.launch { repository.insert(investment) }
    }

    fun delete(investment: Investment) {
        viewModelScope.launch { repository.delete(investment) }
    }

    fun update(investment: Investment) {
        viewModelScope.launch { repository.update(investment) }
    }
}
