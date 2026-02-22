package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.core.InAppBanner
import com.udhaarpay.app.core.InAppBannerManager
import com.udhaarpay.app.repository.CreditCardRepository
import com.udhaarpay.app.repository.BankAccountRepository
import com.udhaarpay.app.repository.ExpenseRepository
import com.udhaarpay.app.data.local.entities.CreditCard
import com.udhaarpay.app.data.local.entities.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditCardViewModel @Inject constructor(
    private val repository: CreditCardRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    val creditCards: StateFlow<List<CreditCard>> =
        repository.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    fun insert(card: CreditCard) {
        viewModelScope.launch {
            repository.insert(card)
            _statusMessage.value = "Card added"
        }
    }

    fun delete(card: CreditCard) {
        viewModelScope.launch {
            repository.delete(card)
            _statusMessage.value = "Card removed"
        }
    }

    fun update(card: CreditCard) {
        viewModelScope.launch {
            repository.update(card)
        }
    }

    fun toggleUpiLink(card: CreditCard, shouldLink: Boolean) {
        viewModelScope.launch {
            if (shouldLink && card.cardType.lowercase() != "rupay") {
                _statusMessage.value = "Only RuPay cards can be linked to UPI."
                return@launch
            }
            repository.update(card.copy(upiLinked = shouldLink))
            _statusMessage.value = if (shouldLink) {
                "Card linked to UPI"
            } else {
                "Card unlinked from UPI"
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun processCardPayment(card: CreditCard, merchantType: String, amount: Double) {
        viewModelScope.launch {
            when {
                merchantType.lowercase() != "merchant" -> {
                    _statusMessage.value =
                        "Only merchant payments are allowed via card. Use bank/UPI for person-to-person payments."
                }
                amount <= 0.0 -> {
                    _statusMessage.value = "Enter a valid amount."
                }
                amount > card.balance -> {
                    _statusMessage.value = "Insufficient available card balance."
                }
                else -> {
                    repository.update(card.copy(balance = card.balance - amount))
                    _statusMessage.value = "Payment successful. INR ${"%.2f".format(amount)} debited."
                    InAppBannerManager.show(
                        InAppBanner(
                            title = "Card Payment",
                            message = "INR ${"%.2f".format(amount)} debited from ${card.issuer} ••••${card.cardNumber}",
                            isCredit = false
                        )
                    )
                }
            }
        }
    }

    fun detectCardType(cardNumber: String): String {
        val clean = cardNumber.filter { it.isDigit() }
        return when {
            clean.startsWith("60") || clean.startsWith("65") || clean.startsWith("81") || clean.startsWith("82") -> "RuPay"
            clean.startsWith("4") -> "Visa"
            clean.startsWith("5") -> "Mastercard"
            else -> "Unknown"
        }
    }

    private fun isLuhnValid(cardNumber: String): Boolean {
        val digits = cardNumber.filter { it.isDigit() }
        if (digits.length < 12) return false
        var sum = 0
        var alternate = false
        for (i in digits.length - 1 downTo 0) {
            var n = digits[i].digitToInt()
            if (alternate) {
                n *= 2
                if (n > 9) n -= 9
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }

    fun addCardWithDetection(
        cardNumber: String,
        issuer: String,
        expiry: String,
        limit: Double
    ) {
        viewModelScope.launch {
            if (cardNumber.length != 16) {
                _statusMessage.value = "Card number must be 16 digits."
                return@launch
            }
            if (!isLuhnValid(cardNumber)) {
                _statusMessage.value = "Card number failed validation."
                return@launch
            }
            val detectedType = detectCardType(cardNumber)
            if (detectedType == "Unknown") {
                _statusMessage.value = "Card type not recognized."
                return@launch
            }
            repository.insert(
                CreditCard(
                    cardNumber = cardNumber.takeLast(4),
                    cardType = detectedType,
                    issuer = issuer,
                    balance = limit, // Available balance
                    limit = limit,
                    expiry = expiry,
                    status = "active",
                    upiLinked = detectedType.equals("RuPay", true)
                )
            )
            _statusMessage.value = if (detectedType == "RuPay") {
                "Card added. RuPay is NFC eligible."
            } else {
                "Card added. NFC is only for RuPay."
            }
        }
    }

    fun payCreditCardBill(card: CreditCard, bankAccountId: Long, amount: Double) {
        viewModelScope.launch {
            val bankAccount = bankAccountRepository.getAll().first().firstOrNull { it.accountId == bankAccountId }
            if (bankAccount == null) {
                _statusMessage.value = "Select a valid bank account."
                return@launch
            }
            if (amount <= 0.0) {
                _statusMessage.value = "Enter a valid bill amount."
                return@launch
            }
            if (bankAccount.balance < amount) {
                _statusMessage.value = "Insufficient bank balance."
                return@launch
            }
            val outstanding = (card.limit - card.balance).coerceAtLeast(0.0)
            val payable = amount.coerceAtMost(outstanding)
            val updatedCardBalance = (card.balance + payable).coerceAtMost(card.limit)

            bankAccountRepository.update(bankAccount.copy(balance = bankAccount.balance - payable))
            repository.update(card.copy(balance = updatedCardBalance))
            expenseRepository.insert(
                Expense(
                    amount = payable,
                    category = "Credit Card Bill",
                    subCategory = card.issuer,
                    account = "bank",
                    accountName = bankAccount.bankName,
                    description = "Card bill payment for ${card.cardNumber}",
                    date = System.currentTimeMillis(),
                    month = java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
                    receiptUrl = null,
                    accountId = bankAccount.accountId
                )
            )
            _statusMessage.value = "Bill paid successfully. INR ${"%.2f".format(payable)}."
            InAppBannerManager.show(
                InAppBanner(
                    title = "Credit Card Bill Paid",
                    message = "INR ${"%.2f".format(payable)} debited from ${bankAccount.bankName}",
                    isCredit = false
                )
            )
        }
    }

}
