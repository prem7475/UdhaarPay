package com.example.udhaarpay.ui.nfc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.dao.NFCTransactionDao
import com.example.udhaarpay.data.dao.RuPayCardDao
import com.example.udhaarpay.data.model.NFCTransaction
import com.example.udhaarpay.data.model.NFCTransactionStatus
import com.example.udhaarpay.data.model.RuPayCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NFCPaymentViewModel @Inject constructor(
    private val ruPayCardDao: RuPayCardDao,
    private val nfcTransactionDao: NFCTransactionDao
) : ViewModel() {

    private val _userCards = MutableStateFlow<List<RuPayCard>>(emptyList())
    val userCards: StateFlow<List<RuPayCard>> = _userCards.asStateFlow()

    private val _nfcTransactions = MutableStateFlow<List<NFCTransaction>>(emptyList())
    val nfcTransactions: StateFlow<List<NFCTransaction>> = _nfcTransactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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
                ruPayCardDao.getUserCards(userId).collect { cards ->
                    _userCards.value = cards
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }

        loadNFCTransactions()
    }

    fun loadNFCTransactions() {
        viewModelScope.launch {
            try {
                nfcTransactionDao.getUserNFCTransactions(userId).collect { transactions ->
                    _nfcTransactions.value = transactions
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun processNFCPayment(cardId: Int, merchantName: String, amount: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val transaction = NFCTransaction(
                    userId = userId,
                    cardId = cardId,
                    merchantName = merchantName,
                    amount = amount,
                    transactionId = UUID.randomUUID().toString(),
                    status = NFCTransactionStatus.SUCCESS
                )
                nfcTransactionDao.insertTransaction(transaction)

                // Update card balance
                val card = ruPayCardDao.getCardById(cardId)
                card?.let {
                    val newBalance = it.availableBalance - amount
                    ruPayCardDao.updateCardBalance(cardId, newBalance)
                }

                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _error.value = null
    }
}
