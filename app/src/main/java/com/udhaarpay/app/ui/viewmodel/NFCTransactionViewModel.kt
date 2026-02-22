package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.data.local.entities.NFCTransactionEntity
import com.udhaarpay.app.repository.NFCTransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NFCTransactionViewModel @Inject constructor(
    repository: NFCTransactionRepository
) : ViewModel() {
    val transactions: StateFlow<List<NFCTransactionEntity>> =
        repository.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
