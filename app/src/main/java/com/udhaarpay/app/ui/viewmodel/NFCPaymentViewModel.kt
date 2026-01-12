package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.data.local.entities.CreditCard
import com.udhaarpay.app.repository.CreditCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// Data class for NFC Transaction
data class NFCTransaction(
    val id: String,
    val card: CreditCard,
    val merchant: String,
    val amount: Double,
    val date: String,
    val status: String
)

@HiltViewModel
class NFCPaymentViewModel @Inject constructor(
    private val creditCardRepository: CreditCardRepository
) : ViewModel() {
    private val _creditCards = MutableStateFlow<List<CreditCard>>(emptyList())
    val creditCards: StateFlow<List<CreditCard>> = _creditCards.asStateFlow()

    private val _selectedCard = MutableStateFlow<CreditCard?>(null)
    val selectedCard: StateFlow<CreditCard?> = _selectedCard.asStateFlow()

    private val _paymentAmount = MutableStateFlow("")
    val paymentAmount: StateFlow<String> = _paymentAmount.asStateFlow()

    private val _isCardExpanded = MutableStateFlow(false)
    val isCardExpanded: StateFlow<Boolean> = _isCardExpanded.asStateFlow()

    private val _nfcStatus = MutableStateFlow("Ready")
    val nfcStatus: StateFlow<String> = _nfcStatus.asStateFlow()

    private val _lastTransaction = MutableStateFlow<NFCTransaction?>(null)
    val lastTransaction: StateFlow<NFCTransaction?> = _lastTransaction.asStateFlow()

    private val merchantNames = listOf(
        "Amazon Retail", "Flipkart Store", "Big Bazaar", "Reliance Mart", "Starbucks", "Apple Store", "Uber India", "Swiggy Food"
    )

    init {
        loadCreditCards()
    }

    private fun loadCreditCards() {
        viewModelScope.launch {
            val cards = creditCardRepository.getAll().firstOrNull()
            if (cards.isNullOrEmpty()) {
                // Mock cards if DB is empty
                val mockCards = listOf(
                    CreditCard(cardId = 1L, cardNumber = "1234", cardType = "RuPay", issuer = "Test Bank", balance = 5000.0, limit = 10000.0, expiry = "12/25", status = "Active", upiLinked = true),
                    CreditCard(cardId = 2L, cardNumber = "5678", cardType = "RuPay", issuer = "Test Bank", balance = 8000.0, limit = 15000.0, expiry = "06/26", status = "Active", upiLinked = true),
                    CreditCard(cardId = 3L, cardNumber = "9012", cardType = "RuPay", issuer = "Test Bank", balance = 3500.0, limit = 7000.0, expiry = "03/27", status = "Active", upiLinked = true)
                )
                _creditCards.value = mockCards
            } else {
                _creditCards.value = cards
            }
        }
    }

    fun getAllCreditCards() = creditCards

    fun getCreditCardById(cardId: Long): CreditCard? {
        return creditCards.value.find { it.cardId == cardId }
    }

    fun selectCard(card: CreditCard) {
        _selectedCard.value = card
    }

    fun expandCardStack() {
        _isCardExpanded.value = true
    }

    fun collapseCardStack() {
        _isCardExpanded.value = false
    }

    fun setPaymentAmount(amount: String) {
        _paymentAmount.value = amount
    }

    fun processNFCPayment(amount: String) {
        val card = _selectedCard.value ?: return
        val amt = amount.toDoubleOrNull() ?: return
        if (amt <= 0.0) return
        if (card.balance < amt) {
            _nfcStatus.value = "Insufficient Balance"
            return
        }
        _nfcStatus.value = "Processing"
        viewModelScope.launch {
            // Simulate payment delay/animation
            kotlinx.coroutines.delay(1800)
            val merchant = merchantNames.random()
            val txnId = "TXN" + System.currentTimeMillis()
            val date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
            val txn = NFCTransaction(
                id = txnId,
                card = card.copy(balance = card.balance - amt),
                merchant = merchant,
                amount = amt,
                date = date,
                status = "Success"
            )
            // Deduct from card balance (mock, not persisted)
            _lastTransaction.value = txn
            _nfcStatus.value = "Success"
            // Optionally update card in DB if needed
        }
    }
}
