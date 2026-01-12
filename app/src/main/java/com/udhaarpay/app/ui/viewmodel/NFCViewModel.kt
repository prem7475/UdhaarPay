package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.data.local.dao.CreditCardDao
import com.udhaarpay.app.data.local.entities.CreditCard
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

    // State for list of cards
    private val _cards = MutableStateFlow<List<CreditCard>>(emptyList())
    val cards: StateFlow<List<CreditCard>> = _cards.asStateFlow()

    // State for selected card
    private val _selectedCard = MutableStateFlow<CreditCard?>(null)
    val selectedCard: StateFlow<CreditCard?> = _selectedCard.asStateFlow()

    init {
        loadCards()
    }

    private fun loadCards() {
        val testUserId = "user_001" // Temporary ID for testing

        viewModelScope.launch {
            creditCardDao.getAll().collect { cardList ->
                _cards.value = cardList

                // If list is empty, let's create a dummy card so you can see the UI
                if (cardList.isEmpty()) {
                    createDummyCard()
                }
            }
        }
    }

    private fun createDummyCard() {
        val dummyCard = CreditCard(
            cardId = 0L,
            cardNumber = "1234",
            cardType = "RuPay",
            issuer = "Test Bank",
            balance = 10000.0,
            limit = 20000.0,
            expiry = "12/34",
            status = "Active",
            upiLinked = true
        )
        viewModelScope.launch {
            creditCardDao.insert(dummyCard)
        }
    }

    fun selectCard(card: CreditCard) {
        _selectedCard.value = card
    }
}