package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.core.InAppBanner
import com.udhaarpay.app.core.InAppBannerManager
import com.udhaarpay.app.data.local.entities.BankAccount
import com.udhaarpay.app.data.local.entities.Debt
import com.udhaarpay.app.data.local.entities.Expense
import com.udhaarpay.app.repository.BankAccountRepository
import com.udhaarpay.app.repository.CreditCardRepository
import com.udhaarpay.app.repository.DebtRepository
import com.udhaarpay.app.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val repository: DebtRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val creditCardRepository: CreditCardRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    val debts: StateFlow<List<Debt>> =
        repository.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<Expense>> =
        expenseRepository.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val linkedAccounts: StateFlow<List<BankAccount>> =
        bankAccountRepository.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalGiven: StateFlow<Double> = repository.getAll()
        .map { debts -> debts.filter { it.type == "given" }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalTaken: StateFlow<Double> = repository.getAll()
        .map { debts -> debts.filter { it.type == "taken" }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netPosition: StateFlow<Double> = repository.getAll()
        .map { debts ->
            val given = debts.filter { it.type == "given" }.sumOf { it.amount }
            val taken = debts.filter { it.type == "taken" }.sumOf { it.amount }
            given - taken
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = expenseRepository.getAll()
        .map { list -> list.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalIncome: StateFlow<Double> = repository.getAll()
        .map { list ->
            list.filter { it.type.equals("taken", true) && it.status.equals("settled", true) }
                .sumOf { it.amountSettled ?: 0.0 }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val spendingByCategory: StateFlow<Map<String, Double>> = expenseRepository.getAll()
        .map { list ->
            list.groupBy { it.category.ifBlank { "Miscellaneous" } }
                .mapValues { (_, values) -> values.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second }
                .toMap()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    private var lastLowBalanceAlertHash: String? = null

    init {
        ensureCoreAccountsExist()
        watchLowBalanceAlerts()
    }

    fun insert(debt: Debt) {
        viewModelScope.launch { repository.insert(debt) }
    }

    fun delete(debt: Debt) {
        viewModelScope.launch { repository.delete(debt) }
    }

    fun update(debt: Debt) {
        viewModelScope.launch { repository.update(debt) }
    }

    fun addDebtEntry(
        personName: String,
        amount: Double,
        type: String,
        category: String,
        paymentSource: String,
        accountId: Long?,
        reason: String?
    ) {
        viewModelScope.launch {
            val entry = Debt(
                personName = personName,
                amount = amount,
                type = type,
                category = category.ifBlank { "Miscellaneous" },
                paymentSource = paymentSource,
                accountId = accountId,
                date = System.currentTimeMillis(),
                reason = reason,
                status = "pending",
                settledDate = null,
                amountSettled = 0.0
            )
            repository.insert(entry)

            val delta = if (type == "given") -amount else amount
            adjustSourceBalance(paymentSource, accountId, delta)
            _statusMessage.value = "Debt entry saved"
        }
    }

    fun settleDebtPartial(
        debt: Debt,
        settledAmount: Double,
        settlementSource: String,
        settlementAccountId: Long?
    ) {
        viewModelScope.launch {
            if (settledAmount <= 0.0) {
                _statusMessage.value = "Settlement amount must be greater than zero."
                return@launch
            }
            val alreadySettled = debt.amountSettled ?: 0.0
            val newSettled = (alreadySettled + settledAmount).coerceAtMost(debt.amount)
            val isFullySettled = newSettled >= debt.amount

            val updated = debt.copy(
                amountSettled = newSettled,
                status = if (isFullySettled) "settled" else "pending",
                settledDate = if (isFullySettled) System.currentTimeMillis() else debt.settledDate
            )
            repository.update(updated)

            val settlementDelta = if (debt.type == "given") settledAmount else -settledAmount
            adjustSourceBalance(settlementSource, settlementAccountId, settlementDelta)
            _statusMessage.value = if (isFullySettled) "Debt settled fully" else "Partial settlement recorded"
        }
    }

    fun updateAccountAmount(accountId: Long, amount: Double) {
        viewModelScope.launch {
            if (amount < 0.0) {
                _statusMessage.value = "Amount cannot be negative."
                return@launch
            }
            val account = linkedAccounts.value.firstOrNull { it.accountId == accountId }
            if (account == null) {
                _statusMessage.value = "Account not found."
                return@launch
            }
            bankAccountRepository.update(account.copy(balance = amount))
            _statusMessage.value = "Updated ${account.bankName} balance."
        }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }

    fun addSpendOrIncomeEntry(
        amount: Double,
        isIncome: Boolean,
        category: String?,
        source: String,
        accountId: Long?,
        description: String?
    ) {
        viewModelScope.launch {
            if (amount <= 0.0) {
                _statusMessage.value = "Amount should be greater than zero."
                return@launch
            }

            val categorySafe = category?.ifBlank { "Miscellaneous" } ?: "Miscellaneous"
            val descriptionSafe = description?.ifBlank { null } ?: if (isIncome) "Income entry" else "Expense entry"

            if (isIncome) {
                val debtIncomeEntry = Debt(
                    personName = "Income",
                    amount = amount,
                    type = "taken",
                    category = categorySafe,
                    paymentSource = source,
                    accountId = accountId,
                    date = System.currentTimeMillis(),
                    reason = descriptionSafe,
                    status = "settled",
                    settledDate = System.currentTimeMillis(),
                    amountSettled = amount
                )
                repository.insert(debtIncomeEntry)
                adjustSourceBalance(source, accountId, amount)
                _statusMessage.value = "Income recorded successfully."
            } else {
                val accountName = linkedAccounts.value.firstOrNull { it.accountId == accountId }?.bankName
                    ?: source.replaceFirstChar { it.uppercase(Locale.getDefault()) }
                expenseRepository.insert(
                    Expense(
                        amount = amount,
                        category = categorySafe,
                        subCategory = null,
                        account = source.lowercase(Locale.getDefault()),
                        accountName = accountName,
                        description = descriptionSafe,
                        date = System.currentTimeMillis(),
                        month = java.text.SimpleDateFormat("MMM yyyy", Locale.getDefault())
                            .format(java.util.Date()),
                        receiptUrl = null,
                        accountId = accountId
                    )
                )
                adjustSourceBalance(source, accountId, -amount)
                _statusMessage.value = "Expense recorded successfully."
            }
        }
    }

    private suspend fun adjustSourceBalance(source: String, accountId: Long?, delta: Double) {
        when (source.lowercase(Locale.getDefault())) {
            "bank" -> {
                if (accountId == null) return
                val account = bankAccountRepository.getAll().first().firstOrNull { it.accountId == accountId } ?: return
                bankAccountRepository.update(account.copy(balance = (account.balance + delta).coerceAtLeast(0.0)))
            }

            "wallet", "cash wallet", "cash" -> {
                if (accountId == null) return
                val wallet = bankAccountRepository.getAll().first().firstOrNull { it.accountId == accountId } ?: return
                bankAccountRepository.update(wallet.copy(balance = (wallet.balance + delta).coerceAtLeast(0.0)))
            }

            "card", "credit card", "credit_card" -> {
                if (accountId == null) return
                val card = creditCardRepository.getAll().first().firstOrNull { it.cardId == accountId } ?: return
                creditCardRepository.update(card.copy(balance = (card.balance + delta).coerceAtLeast(0.0)))
            }
        }
    }

    private fun watchLowBalanceAlerts() {
        viewModelScope.launch {
            combine(linkedAccounts, totalExpense) { accounts, _ ->
                accounts.filter { it.balance <= 1000.0 }
            }.collect { lowAccounts ->
                if (lowAccounts.isNotEmpty()) {
                    val signature = lowAccounts.joinToString("|") { "${it.accountId}:${it.balance.toInt()}" }
                    if (lastLowBalanceAlertHash != signature) {
                        lastLowBalanceAlertHash = signature
                        val first = lowAccounts.first()
                        InAppBannerManager.show(
                            InAppBanner(
                                title = "Low Balance Reminder",
                                message = "${first.bankName} is running low (INR ${"%.2f".format(first.balance)}).",
                                isCredit = false
                            )
                        )
                    }
                }
            }
        }
    }

    private fun ensureCoreAccountsExist() {
        viewModelScope.launch {
            val existing = bankAccountRepository.getAll().first()
            val hasCash = existing.any { it.accountType.equals("Cash", true) || it.bankName.equals("Cash Reserve", true) }
            val hasWallet = existing.any {
                it.accountType.equals("Wallet", true) || it.bankName.equals("Cash Wallet", true)
            }

            if (!hasCash) {
                bankAccountRepository.insert(
                    BankAccount(
                        bankName = "Cash Reserve",
                        accountNumber = "CASH0002",
                        ifscCode = "CASH0000001",
                        accountType = "Cash",
                        balance = 0.0,
                        upiPin = null,
                        nickname = "Cash",
                        addedDate = System.currentTimeMillis()
                    )
                )
            }
            if (!hasWallet) {
                bankAccountRepository.insert(
                    BankAccount(
                        bankName = "Cash Wallet",
                        accountNumber = "WALLET0001",
                        ifscCode = "WALT0000001",
                        accountType = "Wallet",
                        balance = 0.0,
                        upiPin = null,
                        nickname = "Wallet",
                        addedDate = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}
