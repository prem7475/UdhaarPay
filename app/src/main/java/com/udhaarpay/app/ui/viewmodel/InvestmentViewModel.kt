package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.repository.InvestmentRepository
import com.udhaarpay.app.data.local.entities.Investment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import javax.inject.Inject

@HiltViewModel
class InvestmentViewModel @Inject constructor(
    private val repository: InvestmentRepository
) : ViewModel() {
    val investments: StateFlow<List<Investment>> =
        repository.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val summary: StateFlow<Double?> =
        repository.getSummary().stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalInvested: StateFlow<Double> = repository.getAll()
        .map { items -> items.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalCurrent: StateFlow<Double> = repository.getAll()
        .map { items -> items.sumOf { it.currentValue ?: it.amount } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val profitLoss: StateFlow<Double> = repository.getAll()
        .map { items ->
            items.sumOf { (it.currentValue ?: it.amount) - it.amount }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    init {
        simulateGrowthOnLaunch()
    }

    fun insert(investment: Investment) {
        viewModelScope.launch {
            repository.insert(investment)
            _statusMessage.value = "Investment added"
        }
    }

    fun delete(investment: Investment) {
        viewModelScope.launch { repository.delete(investment) }
    }

    fun update(investment: Investment) {
        viewModelScope.launch { repository.update(investment) }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }

    private fun simulateGrowthOnLaunch() {
        viewModelScope.launch {
            val currentList = repository.getAll().first()
            if (currentList.isEmpty()) return@launch
            currentList.forEach { item ->
                val current = item.currentValue ?: item.amount
                val growthPct = generateGrowthPercent(item.investmentId)
                val updated = item.copy(
                    currentValue = current * (1 + growthPct / 100.0),
                    returns = ((current * (1 + growthPct / 100.0)) - item.amount)
                )
                repository.update(updated)
            }
            _statusMessage.value = "Portfolio updated with mock market movement."
        }
    }

    private fun generateGrowthPercent(seed: Long): Double {
        val hash = (seed.toString() + System.currentTimeMillis().toString().takeLast(4)).hashCode().absoluteValue
        return 0.5 + (hash % 150) / 100.0
    }
}
