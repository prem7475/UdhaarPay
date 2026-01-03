package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.model.CreditCard
import com.example.udhaarpay.data.repository.CreditCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditCardViewModel @Inject constructor(
    private val repository: CreditCardRepository
) : ViewModel() {
    
    private val _creditCards = MutableStateFlow<List<CreditCard>>(emptyList())
    val creditCards: StateFlow<List<CreditCard>> = _creditCards.asStateFlow()
    
    private val _defaultCard = MutableStateFlow<CreditCard?>(null)
    val defaultCard: StateFlow<CreditCard?> = _defaultCard.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()
    
    private var currentUserId = "user123" // Mock user ID
    
    init {
        loadUserCards()
    }
    
    fun setUserId(userId: String) {
        currentUserId = userId
        loadUserCards()
    }
    
    private fun loadUserCards() {
        viewModelScope.launch {
            repository.getUserCards(currentUserId).collect { cards ->
                _creditCards.value = cards
                // Update default card
                _defaultCard.value = cards.find { it.isDefault } ?: cards.firstOrNull()
            }
        }
    }
    
    fun addCard(
        cardNumber: String,
        cardholderName: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvv: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _success.value = null
            
            val result = repository.addCard(
                userId = currentUserId,
                cardNumber = cardNumber,
                cardholderName = cardholderName,
                expiryMonth = expiryMonth,
                expiryYear = expiryYear,
                cvv = cvv
            )
            
            result.onSuccess { card ->
                _success.value = "Card added successfully"
                loadUserCards()
            }
            
            result.onFailure { exception ->
                _error.value = exception.message ?: "Failed to add card"
            }
            
            _isLoading.value = false
        }
    }
    
    fun setDefaultCard(cardId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.setDefaultCard(cardId)
            
            result.onSuccess {
                _success.value = "Default card updated"
                loadUserCards()
            }
            
            result.onFailure { exception ->
                _error.value = exception.message ?: "Failed to set default card"
            }
            
            _isLoading.value = false
        }
    }
    
    fun deleteCard(cardId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.deleteCard(cardId)
            
            result.onSuccess {
                _success.value = "Card deleted successfully"
                loadUserCards()
            }
            
            result.onFailure { exception ->
                _error.value = exception.message ?: "Failed to delete card"
            }
            
            _isLoading.value = false
        }
    }
    
    fun clearMessages() {
        _error.value = null
        _success.value = null
    }
}
