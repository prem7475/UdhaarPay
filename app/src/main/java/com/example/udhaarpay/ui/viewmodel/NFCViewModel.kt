package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.udhaarpay.data.model.BankCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class NFCViewModel @Inject constructor() : ViewModel() {
    private val _selectedCard = MutableStateFlow<BankCard?>(null)
    val selectedCard: StateFlow<BankCard?> = _selectedCard.asStateFlow()

    fun setSelectedCard(card: BankCard?) {
        _selectedCard.value = card
    }
}
