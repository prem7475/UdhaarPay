package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.CreditCardDao
import com.example.udhaarpay.data.local.entity.CreditCardEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NFCViewModel @Inject constructor(
    private val creditCardDao: CreditCardDao
) : ViewModel() {

    private val _cards = MutableStateFlow<List<CreditCardEntity>>(emptyList())
    val cards: StateFlow<List<CreditCardEntity>> = _cards.asStateFlow()

    private val _selectedCard = MutableStateFlow<CreditCardEntity?>(null)
    val selectedCard: StateFlow<CreditCardEntity?> = _selectedCard.asStateFlow()

    init {
        loadCards()
    }

    private fun loadCards() {
        viewModelScope.launch {
            creditCardDao.getAllCreditCards().collect { cardList ->
                _cards.value = cardList
                if (cardList.isNotEmpty() && _selectedCard.value == null) {
                    _selectedCard.value = cardList[0] // Select first by default
                }
            }
        }
    }

    fun selectCard(card: CreditCardEntity) {
        _selectedCard.value = card
    }
}
