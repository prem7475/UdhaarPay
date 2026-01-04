package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.CreditCardDao
import com.example.udhaarpay.data.model.CreditCard
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
            creditCardDao.getUserCards(testUserId).collect { cardList ->
                _cards.value = cardList

                // If list is empty, let's create a dummy card so you can see the UI
                if (cardList.isEmpty()) {
                    createDummyCard(testUserId)
                }
            }
        }
    }

    fun selectCard(card: CreditCard) {
        _selectedCard.value = card
    }

    private fun createDummyCard(userId: String) {
        viewModelScope.launch {
            val dummy = CreditCard(
                userId = userId,
                cardNumber = "4111222233339999",
                cardHolderName = "Prem User",
                expiryMonth = 12,
                expiryYear = 2028,
                cvv = "123",
                issuerBank = "HDFC Bank",
                cardType = "VISA",
                cardColor = "#1E3A8A",
                limit = 100000.0,
                balanceUsed = 15000.0,
                isDefault = true
            )
            creditCardDao.insertCard(dummy)
        }
    }
}