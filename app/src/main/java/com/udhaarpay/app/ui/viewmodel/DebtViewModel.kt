package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.repository.DebtRepository
import com.udhaarpay.app.data.local.entities.Debt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val repository: DebtRepository
) : ViewModel() {
    val debts: StateFlow<List<Debt>> =
        repository.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(debt: Debt) {
        viewModelScope.launch { repository.insert(debt) }
    }

    fun delete(debt: Debt) {
        viewModelScope.launch { repository.delete(debt) }
    }

    fun update(debt: Debt) {
        viewModelScope.launch { repository.update(debt) }
    }
}
