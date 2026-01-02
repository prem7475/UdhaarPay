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
import kotlin.random.Random

@HiltViewModel
class CardViewModel @Inject constructor(
    private val creditCardDao: CreditCardDao
) : ViewModel() {

    private val _addCardState = MutableStateFlow<AddCardResult?>(null)
    val addCardState: StateFlow<AddCardResult?> = _addCardState.asStateFlow()

    private val _cards = MutableStateFlow<List<CreditCardEntity>>(emptyList())
    val cards: StateFlow<List<CreditCardEntity>> = _cards.asStateFlow()

    init {
        loadCards()
    }

    private fun loadCards() {
        viewModelScope.launch {
            creditCardDao.getAllCreditCards().collect {
                _cards.value = it
            }
        }
    }

    fun validateAndAddCard(number: String, name: String, expiry: String, cvv: String) {
        // Clean the input number
        val cleanNumber = number.replace(" ", "").replace("-", "")
        
        // Rupay Validation Logic
        val isRupay = cleanNumber.startsWith("60") || 
                      cleanNumber.startsWith("65") || 
                      cleanNumber.startsWith("81") || 
                      cleanNumber.startsWith("82")

        if (!isRupay) {
            _addCardState.value = AddCardResult.Error("Only Rupay Credit Cards are allowed on UPI")
            return
        }

        if (cleanNumber.length < 16) {
             _addCardState.value = AddCardResult.Error("Invalid Card Number")
             return
        }

        viewModelScope.launch {
            // Mock limit and used balance
            val mockLimit = Random.nextDouble(50000.0, 200000.0)
            val mockUsed = Random.nextDouble(0.0, mockLimit * 0.4)

            val newCard = CreditCardEntity(
                name = "My Rupay Card", // Default name or parse bank from BIN
                number = cleanNumber,
                expiry = expiry,
                cvv = cvv,
                limit = mockLimit,
                balanceUsed = mockUsed,
                isRupay = true
            )
            
            creditCardDao.insertCreditCard(newCard)
            _addCardState.value = AddCardResult.Success
        }
    }

    fun resetAddCardState() {
        _addCardState.value = null
    }
}

sealed class AddCardResult {
    object Success : AddCardResult()
    data class Error(val message: String) : AddCardResult()
}
