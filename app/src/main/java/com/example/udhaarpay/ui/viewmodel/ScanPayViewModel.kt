package com.example.udhaarpay.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.BankAccountDao
import com.example.udhaarpay.data.local.dao.CreditCardDao
import com.example.udhaarpay.data.local.dao.TransactionDao
import com.example.udhaarpay.data.local.entity.BankAccountEntity
import com.example.udhaarpay.data.local.entity.CreditCardEntity
import com.example.udhaarpay.data.local.entity.TransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScanPayState(
    val isQrProcessed: Boolean = false,
    val payeeName: String = "",
    val payeeVpa: String = "",
    val isMerchant: Boolean = false,
    val bankAccounts: List<BankAccountEntity> = emptyList(),
    val creditCards: List<CreditCardEntity> = emptyList(),
    val selectedSourceId: String? = null,
    val amount: String = "", // Added amount
    val isLoading: Boolean = false, // Added loading state
    val isSuccess: Boolean = false, // Added success state
    val error: String? = null
)

@HiltViewModel
class ScanPayViewModel @Inject constructor(
    private val bankAccountDao: BankAccountDao,
    private val creditCardDao: CreditCardDao,
    private val transactionDao: TransactionDao
) : ViewModel() {

    private val _state = MutableStateFlow(ScanPayState())
    val state: StateFlow<ScanPayState> = _state.asStateFlow()

    init {
        loadPaymentSources()
    }

    private fun loadPaymentSources() {
        viewModelScope.launch {
            bankAccountDao.getAllBankAccounts().collect { banks ->
                _state.value = _state.value.copy(bankAccounts = banks)
            }
        }
        viewModelScope.launch {
            creditCardDao.getAllCreditCards().collect { cards ->
                _state.value = _state.value.copy(creditCards = cards)
            }
        }
    }

    fun processQrCode(qrString: String) {
        try {
            val uri = Uri.parse(qrString)
            val vpa = uri.getQueryParameter("pa")
            val name = uri.getQueryParameter("pn")
            val mcc = uri.getQueryParameter("mcc") // Merchant Category Code

            if (vpa == null) {
                _state.value = _state.value.copy(error = "Invalid QR Code: Missing VPA")
                return
            }

            val isMerchant = !mcc.isNullOrEmpty()

            _state.value = _state.value.copy(
                isQrProcessed = true,
                payeeName = name ?: vpa,
                payeeVpa = vpa,
                isMerchant = isMerchant,
                selectedSourceId = null, // Reset selection
                error = null
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = "Error parsing QR Code")
        }
    }

    fun selectPaymentSource(sourceId: String) {
        _state.value = _state.value.copy(selectedSourceId = sourceId)
    }

    fun setAmount(amount: String) {
        _state.value = _state.value.copy(amount = amount)
    }

    fun processPayment() {
        val amountVal = _state.value.amount.toDoubleOrNull()
        if (amountVal == null || amountVal <= 0) {
            _state.value = _state.value.copy(error = "Please enter a valid amount")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            // Simulate network delay
            delay(2000)

            // 1. Create Transaction
            val transaction = TransactionEntity(
                amount = amountVal,
                type = "Debit",
                category = "Payment", // Generic for now
                description = "Paid to ${_state.value.payeeName}", // Using description if available or just mapping logic
                // The entity definition from earlier prompt didn't have description, let me check.
                // Ah, earlier TransactionEntity was: id, amount, type, category, date, status.
                // ExpenseEntity had description. TransactionEntity might not.
                // Checking previous TransactionEntity definition:
                // id: Long, amount: Double, type: String, category: String, date: Long, status: String
                date = System.currentTimeMillis(),
                status = "Success"
            )
            transactionDao.insertTransaction(transaction)

            // 2. Deduct Amount (Simulation)
            // Ideally we would update the BankAccountEntity or CreditCardEntity here.
            // For Wallet/Bank, we might update balance. For Card, update used balance.
            // Let's implement deduction logic based on selected source.
            val sourceId = _state.value.selectedSourceId
            if (sourceId != null) {
                if (sourceId.startsWith("bank_")) {
                    val bankId = sourceId.removePrefix("bank_").toLongOrNull()
                    if (bankId != null) {
                        val bank = bankAccountDao.getBankAccountById(bankId)
                        if (bank != null) {
                            val newBalance = bank.balance - amountVal
                            bankAccountDao.insertBankAccount(bank.copy(balance = newBalance))
                        }
                    }
                }
                // Similar logic for Credit Card (increase used balance) can be added here
            }

            _state.value = _state.value.copy(isLoading = false, isSuccess = true)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
