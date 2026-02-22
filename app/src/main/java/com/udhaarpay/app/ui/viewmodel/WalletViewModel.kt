package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.core.InAppBanner
import com.udhaarpay.app.core.InAppBannerManager
import com.udhaarpay.app.data.local.entities.BankAccount
import com.udhaarpay.app.data.local.entities.Expense
import com.udhaarpay.app.repository.BankAccountRepository
import com.udhaarpay.app.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val bankAccountRepository: BankAccountRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    val walletAccount: StateFlow<BankAccount?> = bankAccountRepository.getAll()
        .map { accounts ->
            accounts.firstOrNull {
                it.accountType.equals("Wallet", true) || it.bankName.equals("Cash Wallet", true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val walletTransactions: StateFlow<List<Expense>> = expenseRepository.getAll()
        .map { expenses ->
            expenses.filter { it.account.equals("wallet", true) }.sortedByDescending { it.date }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    init {
        ensureWalletExists()
    }

    fun addMoney(amount: Double, note: String?) {
        if (amount <= 0) {
            _statusMessage.value = "Enter a valid amount."
            return
        }
        viewModelScope.launch {
            val wallet = walletAccount.value ?: return@launch
            val updated = wallet.copy(balance = wallet.balance + amount)
            bankAccountRepository.update(updated)
            recordWalletExpense(
                wallet = updated,
                amount = amount,
                category = "Wallet Topup",
                description = note?.ifBlank { null } ?: "Wallet top-up"
            )
            _statusMessage.value = "Money added to wallet."
            InAppBannerManager.show(
                InAppBanner(
                    title = "Wallet Credited",
                    message = "INR ${"%.2f".format(amount)} credited to ${wallet.bankName}",
                    isCredit = true
                )
            )
        }
    }

    fun spendMoney(amount: Double, note: String?) {
        if (amount <= 0) {
            _statusMessage.value = "Enter a valid amount."
            return
        }
        viewModelScope.launch {
            val wallet = walletAccount.value ?: return@launch
            if (wallet.balance < amount) {
                _statusMessage.value = "Insufficient wallet balance."
                return@launch
            }
            val updated = wallet.copy(balance = wallet.balance - amount)
            bankAccountRepository.update(updated)
            recordWalletExpense(
                wallet = updated,
                amount = amount,
                category = "Wallet Spend",
                description = note?.ifBlank { null } ?: "Wallet expense"
            )
            _statusMessage.value = "Wallet expense recorded."
            InAppBannerManager.show(
                InAppBanner(
                    title = "Wallet Debited",
                    message = "INR ${"%.2f".format(amount)} debited from ${wallet.bankName}",
                    isCredit = false
                )
            )
        }
    }

    fun withdrawMoney(amount: Double, note: String?) {
        if (amount <= 0) {
            _statusMessage.value = "Enter a valid amount."
            return
        }
        viewModelScope.launch {
            val wallet = walletAccount.value ?: return@launch
            if (wallet.balance < amount) {
                _statusMessage.value = "Insufficient wallet balance."
                return@launch
            }
            val updated = wallet.copy(balance = wallet.balance - amount)
            bankAccountRepository.update(updated)
            recordWalletExpense(
                wallet = updated,
                amount = amount,
                category = "Wallet Withdraw",
                description = note?.ifBlank { null } ?: "Cash withdrawal from wallet"
            )
            _statusMessage.value = "Money withdrawn from wallet."
            InAppBannerManager.show(
                InAppBanner(
                    title = "Wallet Debited",
                    message = "INR ${"%.2f".format(amount)} withdrawn from ${wallet.bankName}",
                    isCredit = false
                )
            )
        }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }

    private fun ensureWalletExists() {
        viewModelScope.launch {
            val existingWallet = bankAccountRepository.getAll().first().firstOrNull {
                it.accountType.equals("Wallet", true) || it.bankName.equals("Cash Wallet", true)
            }
            if (existingWallet == null) {
                bankAccountRepository.insert(
                    BankAccount(
                        bankName = "Cash Wallet",
                        accountNumber = "CASH0001",
                        ifscCode = "WALLET000001",
                        accountType = "Wallet",
                        balance = 0.0,
                        upiPin = "1234",
                        nickname = "Cash",
                        addedDate = System.currentTimeMillis()
                    )
                )
            } else if (existingWallet.upiPin.isNullOrBlank()) {
                bankAccountRepository.update(existingWallet.copy(upiPin = "1234"))
            }
        }
    }

    private suspend fun recordWalletExpense(
        wallet: BankAccount,
        amount: Double,
        category: String,
        description: String
    ) {
        expenseRepository.insert(
            Expense(
                amount = amount,
                category = category,
                subCategory = "Cash Wallet",
                account = "wallet",
                accountName = wallet.bankName,
                description = description,
                date = System.currentTimeMillis(),
                month = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date()),
                receiptUrl = null,
                accountId = wallet.accountId
            )
        )
    }
}
