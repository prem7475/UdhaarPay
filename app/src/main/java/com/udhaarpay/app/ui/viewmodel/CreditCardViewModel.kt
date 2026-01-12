package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.repository.CreditCardRepository
import com.udhaarpay.app.data.local.entities.CreditCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditCardViewModel @Inject constructor(
    private val repository: CreditCardRepository
) : ViewModel() {
    val creditCards: StateFlow<List<CreditCard>> =
        repository.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(card: CreditCard) {
        viewModelScope.launch { repository.insert(card) }
    }

    fun delete(card: CreditCard) {
        viewModelScope.launch { repository.delete(card) }
    }

    fun update(card: CreditCard) {
        viewModelScope.launch { repository.update(card) }
    }
}
