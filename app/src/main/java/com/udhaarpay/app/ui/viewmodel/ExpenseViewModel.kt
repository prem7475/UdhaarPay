package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.repository.ExpenseRepository
import com.udhaarpay.app.data.local.entities.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {
    val expenses: StateFlow<List<Expense>> =
        repository.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(expense: Expense) {
        viewModelScope.launch { repository.insert(expense) }
    }

    fun delete(expense: Expense) {
        viewModelScope.launch { repository.delete(expense) }
    }

    fun update(expense: Expense) {
        viewModelScope.launch { repository.update(expense) }
    }
}
