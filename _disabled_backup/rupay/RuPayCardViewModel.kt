package com.example.udhaarpay.ui.rupay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.dao.RuPayCardDao
import com.example.udhaarpay.data.model.RuPayCard
import com.example.udhaarpay.data.model.RuPayCardType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RuPayCardViewModel @Inject constructor(
    private val ruPayCardDao: RuPayCardDao
) : ViewModel() {

    private val _cards = MutableStateFlow<List<RuPayCard>>(emptyList())
    val cards: StateFlow<List<RuPayCard>> = _cards.asStateFlow()

    private val _defaultCard = MutableStateFlow<RuPayCard?>(null)
    val defaultCard: StateFlow<RuPayCard?> = _defaultCard.asStateFlow()

    private val _totalBalance = MutableStateFlow(0.0)
    val totalBalance: StateFlow<Double> = _totalBalance.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cardAdded = MutableStateFlow(false)
    val cardAdded: StateFlow<Boolean> = _cardAdded.asStateFlow()

    private var userId: Int = 1

    init {
        loadUserCards()
    }

    fun setUserId(userId: Int) {
        this.userId = userId
        loadUserCards()
    }

    fun loadUserCards() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                ruPayCardDao.getUserCards(userId).collect { cardsList ->
                    _cards.value = cardsList
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }

        viewModelScope.launch {
            ruPayCardDao.getDefaultCard(userId).collect { card ->
                _defaultCard.value = card
            }
        }

        viewModelScope.launch {
            ruPayCardDao.getTotalBalance(userId).collect { balance ->
                _totalBalance.value = balance ?: 0.0
            }
        }
    }

    fun addRuPayCard(
        cardNumber: String,
        cardHolderName: String,
        expiryDate: String,
        cvv: String,
        cardType: RuPayCardType = RuPayCardType.STANDARD,
        creditLimit: Double = 0.0
    ) {
        if (!RuPayCard.isValidRuPayCard(cardNumber)) {
            _error.value = "Invalid RuPay card number"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val last4 = cardNumber.takeLast(4)
                val card = RuPayCard(
                    userId = userId,
                    cardNumber = cardNumber,
                    cardHolderName = cardHolderName,
                    expiryDate = expiryDate,
                    cvv = cvv,
                    cardType = cardType,
                    creditLimit = creditLimit,
                    availableBalance = creditLimit,
                    isDefault = _cards.value.isEmpty(),
                    last4Digits = last4
                )
                ruPayCardDao.insertCard(card)
                _cardAdded.value = true
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCard(card: RuPayCard) {
        viewModelScope.launch {
            try {
                ruPayCardDao.deleteCard(card)
                if (card.isDefault && _cards.value.isNotEmpty()) {
                    val nextCard = _cards.value.firstOrNull { it.id != card.id }
                    nextCard?.let { setDefaultCard(it.id) }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun setDefaultCard(cardId: Int) {
        viewModelScope.launch {
            try {
                ruPayCardDao.clearDefaultCard(userId)
                ruPayCardDao.setDefaultCard(cardId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateCardBalance(cardId: Int, newBalance: Double) {
        viewModelScope.launch {
            try {
                ruPayCardDao.updateCardBalance(cardId, newBalance)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _cardAdded.value = false
    }
}
