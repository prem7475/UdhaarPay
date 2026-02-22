package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.core.InAppBanner
import com.udhaarpay.app.core.InAppBannerManager
import com.udhaarpay.app.data.local.entities.CreditCard
import com.udhaarpay.app.data.local.entities.NFCTransactionEntity
import com.udhaarpay.app.repository.CreditCardRepository
import com.udhaarpay.app.repository.NFCTransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

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
    private val creditCardRepository: CreditCardRepository,
    private val nfcTransactionRepository: NFCTransactionRepository
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

    private val _nfcTransactions = MutableStateFlow<List<NFCTransactionEntity>>(emptyList())
    val nfcTransactions: StateFlow<List<NFCTransactionEntity>> = _nfcTransactions.asStateFlow()

    private val _rewardAmount = MutableStateFlow(0.0)
    val rewardAmount: StateFlow<Double> = _rewardAmount.asStateFlow()

    private val merchantNames = listOf(
        "Amazon Retail",
        "Flipkart Store",
        "Big Bazaar",
        "Reliance Mart",
        "Starbucks",
        "Apple Store",
        "Uber India",
        "Swiggy Food"
    )

    init {
        observeCardsAndTransactions()
    }

    private fun observeCardsAndTransactions() {
        viewModelScope.launch {
            creditCardRepository.getAll().collect { cards ->
                _creditCards.value = cards
                val currentSelectedId = _selectedCard.value?.cardId
                _selectedCard.value = when {
                    cards.isEmpty() -> null
                    currentSelectedId == null -> cards.first()
                    else -> cards.firstOrNull { it.cardId == currentSelectedId } ?: cards.first()
                }
            }
        }

        viewModelScope.launch {
            nfcTransactionRepository.getAll().collect { items ->
                _nfcTransactions.value = items
            }
        }
    }

    fun getAllCreditCards() = creditCards

    fun getCreditCardById(cardId: Long): CreditCard? {
        return creditCards.value.find { it.cardId == cardId }
    }

    fun selectCard(card: CreditCard) {
        _selectedCard.value = card
        if (_nfcStatus.value !in listOf("Processing", "Scanning")) {
            _nfcStatus.value = "Ready"
        }
    }

    fun expandCardStack() {
        _isCardExpanded.value = true
    }

    fun collapseCardStack() {
        _isCardExpanded.value = false
    }

    fun setPaymentAmount(amount: String) {
        _paymentAmount.value = amount
        if (_nfcStatus.value !in listOf("Processing", "Scanning")) {
            _nfcStatus.value = "Ready"
        }
    }

    fun resetReadyState() {
        if (_nfcStatus.value !in listOf("Processing", "Scanning")) {
            _nfcStatus.value = "Ready"
        }
    }

    fun processNFCPayment(amount: String) {
        val card = _selectedCard.value ?: return
        val amt = amount.toDoubleOrNull() ?: return
        if (amt <= 0.0) return

        if (!card.cardType.equals("rupay", ignoreCase = true)) {
            _nfcStatus.value = "RuPay cards only for NFC"
            return
        }
        if (card.balance < amt) {
            _nfcStatus.value = "Insufficient Balance"
            return
        }

        _nfcStatus.value = "Scanning"
        viewModelScope.launch {
            delay(450)
            _nfcStatus.value = "Processing"
            delay(1050)

            val merchant = merchantNames.random()
            val txnId = "TXN${System.currentTimeMillis()}"
            val now = System.currentTimeMillis()
            val dateText = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(now))
            val reward = (amt * 0.01).coerceAtLeast(1.0)

            val updatedCard = card.copy(balance = card.balance - amt)
            creditCardRepository.update(updatedCard)

            nfcTransactionRepository.insert(
                NFCTransactionEntity(
                    transactionRef = txnId,
                    cardId = card.cardId,
                    cardLast4 = card.cardNumber.takeLast(4),
                    amount = amt,
                    merchant = merchant,
                    timestamp = now,
                    status = "success",
                    rewardEarned = reward
                )
            )

            _selectedCard.value = updatedCard
            _lastTransaction.value = NFCTransaction(
                id = txnId,
                card = updatedCard,
                merchant = merchant,
                amount = amt,
                date = dateText,
                status = "Success"
            )
            _rewardAmount.value = reward
            _nfcStatus.value = "Success"
            _paymentAmount.value = ""

            InAppBannerManager.show(
                InAppBanner(
                    title = "NFC Payment Successful",
                    message = "INR ${"%.2f".format(amt)} debited from ${card.issuer} ****${card.cardNumber.takeLast(4)} at $dateText",
                    isCredit = false
                )
            )
        }
    }
}
