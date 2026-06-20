package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.core.InAppBanner
import com.udhaarpay.app.core.InAppBannerManager
import com.udhaarpay.app.data.local.dao.UserProfileDao
import com.udhaarpay.app.data.local.entities.BankAccount
import com.udhaarpay.app.data.local.entities.Expense
import com.udhaarpay.app.data.local.entities.UPIPayment
import com.udhaarpay.app.data.local.entities.UserProfile
import com.udhaarpay.app.repository.BankAccountRepository
import com.udhaarpay.app.repository.CreditCardRepository
import com.udhaarpay.app.repository.ExpenseRepository
import com.udhaarpay.app.repository.UPIPaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class PaymentContact(
    val name: String,
    val phone: String,
    val upiId: String
)

@HiltViewModel
class UPIPaymentViewModel @Inject constructor(
    private val upiPaymentRepository: UPIPaymentRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val creditCardRepository: CreditCardRepository,
    private val expenseRepository: ExpenseRepository,
    private val userProfileDao: UserProfileDao
) : ViewModel() {

    val payments: StateFlow<List<UPIPayment>> =
        upiPaymentRepository.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bankAccounts: StateFlow<List<BankAccount>> =
        bankAccountRepository.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val creditCards = creditCardRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUser: StateFlow<UserProfile?> = userProfileDao.getAll()
        .map { it.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val walletAccount: StateFlow<BankAccount?> = bankAccountRepository.getAll()
        .map { accounts ->
            accounts.firstOrNull {
                it.accountType.equals("Wallet", true) || it.bankName.equals("Cash Wallet", true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _contacts = MutableStateFlow(
        listOf(
            PaymentContact("Rajesh Kumar", "9876543210", "rajesh@okicici"),
            PaymentContact("Priya Singh", "9811122233", "priya@okhdfcbank"),
            PaymentContact("Amit Patel", "9800012345", "amit@oksbi"),
            PaymentContact("Sana Khan", "9898989898", "sana@okaxis"),
            PaymentContact("Rohit Verma", "9765432109", "rohit@paytm"),
            PaymentContact("Neha Sharma", "9797979797", "neha@ybl")
        )
    )
    val contacts: StateFlow<List<PaymentContact>> = _contacts.asStateFlow()

    val paymentCategories = listOf(
        "Transportation",
        "Salary",
        "Beauty",
        "Books",
        "Shopping",
        "Eats",
        "Bills",
        "Health",
        "Travel",
        "Miscellaneous"
    )

    fun setWalletPinFreeLimit(limit: Double) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            if (limit <= 0.0) {
                _statusMessage.value = "Wallet no-pin limit must be greater than zero."
                return@launch
            }
            userProfileDao.update(user.copy(walletPinFreeLimit = limit))
            _statusMessage.value = "Wallet no-pin limit set to INR ${"%.2f".format(limit)}"
        }
    }

    fun payFromContact(
        contact: PaymentContact,
        amount: Double,
        sourceType: String,
        sourceId: Long?,
        category: String?,
        note: String?,
        enteredPin: String?
    ) {
        sendPayment(
            recipientName = contact.name,
            recipientUpi = contact.upiId,
            amount = amount,
            sourceType = sourceType,
            sourceId = sourceId,
            category = category,
            note = note,
            enteredPin = enteredPin,
            isSelfTransfer = false
        )
    }

    fun payToUpi(
        recipientUpi: String,
        amount: Double,
        sourceType: String,
        sourceId: Long?,
        category: String?,
        note: String?,
        enteredPin: String?
    ) {
        sendPayment(
            recipientName = recipientUpi.substringBefore("@").ifBlank { "UPI Receiver" },
            recipientUpi = recipientUpi,
            amount = amount,
            sourceType = sourceType,
            sourceId = sourceId,
            category = category,
            note = note,
            enteredPin = enteredPin,
            isSelfTransfer = false
        )
    }

    fun payToAccountNumber(
        accountNumber: String,
        ifsc: String,
        amount: Double,
        sourceType: String,
        sourceId: Long?,
        category: String?,
        note: String?,
        enteredPin: String?
    ) {
        val cleanAccount = accountNumber.filter { it.isDigit() }.takeLast(6)
        val cleanIfsc = ifsc.uppercase(Locale.getDefault()).take(4)
        val mockUpi = "${cleanAccount.ifBlank { "acc" }}@${cleanIfsc.ifBlank { "bank" }}"
        sendPayment(
            recipientName = "Account Transfer",
            recipientUpi = mockUpi,
            amount = amount,
            sourceType = sourceType,
            sourceId = sourceId,
            category = category,
            note = note,
            enteredPin = enteredPin,
            isSelfTransfer = false
        )
    }

    fun transferToSelfWallet(
        amount: Double,
        sourceType: String,
        sourceId: Long?,
        note: String?,
        enteredPin: String?
    ) {
        sendPayment(
            recipientName = "Self Wallet",
            recipientUpi = "self@udhaarpay",
            amount = amount,
            sourceType = sourceType,
            sourceId = sourceId,
            category = "Self Transfer",
            note = note,
            enteredPin = enteredPin,
            isSelfTransfer = true
        )
    }

    fun requestMoney(
        requesterUpi: String,
        amount: Double,
        note: String?
    ) {
        val userUpi = currentUser.value?.upiId ?: "me@udhaarpay"
        if (requesterUpi.isBlank() || amount <= 0.0) {
            _statusMessage.value = "Requester and amount are required."
            return
        }

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            upiPaymentRepository.insert(
                UPIPayment(
                    senderUPI = requesterUpi,
                    recipientUPI = userUpi,
                    amount = amount,
                    date = now,
                    message = note?.ifBlank { null },
                    status = "Pending",
                    type = "request"
                )
            )
            _statusMessage.value = "Payment request created."
            InAppBannerManager.show(
                InAppBanner(
                    title = "Payment Request Sent",
                    message = "Requested INR ${"%.2f".format(amount)} from $requesterUpi",
                    isCredit = false
                )
            )
        }
    }

    fun markRequestAsReceived(
        payment: UPIPayment,
        destinationType: String = "bank",
        destinationId: Long? = null
    ) {
        if (!payment.type.equals("request", true)) return
        if (payment.status.equals("received", true)) {
            _statusMessage.value = "Request already received."
            return
        }

        viewModelScope.launch {
            val destination = when (destinationType.lowercase(Locale.getDefault())) {
                "wallet" -> walletAccount.value
                else -> {
                    if (destinationId != null) {
                        bankAccounts.value.firstOrNull { it.accountId == destinationId }
                    } else {
                        bankAccounts.value.firstOrNull { !it.accountType.equals("wallet", true) }
                    }
                }
            }

            if (destination == null) {
                _statusMessage.value = "Add an account or wallet to receive payment."
                return@launch
            }

            bankAccountRepository.update(destination.copy(balance = destination.balance + payment.amount))
            val receivedAt = System.currentTimeMillis()
            upiPaymentRepository.update(
                payment.copy(
                    status = "Received",
                    type = "received",
                    date = receivedAt
                )
            )

            val timeText = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(receivedAt))
            InAppBannerManager.show(
                InAppBanner(
                    title = "Payment Received",
                    message = "INR ${"%.2f".format(payment.amount)} credited to ${destination.bankName} at $timeText",
                    isCredit = true
                )
            )
            _statusMessage.value = "Payment marked as received."
        }
    }

    private fun sendPayment(
        recipientName: String,
        recipientUpi: String,
        amount: Double,
        sourceType: String,
        sourceId: Long?,
        category: String?,
        note: String?,
        enteredPin: String?,
        isSelfTransfer: Boolean
    ) {
        viewModelScope.launch {
            if (amount <= 0.0) {
                _statusMessage.value = "Enter a valid amount."
                return@launch
            }

            if (!isSelfTransfer && recipientUpi.isBlank()) {
                _statusMessage.value = "Recipient UPI not available."
                return@launch
            }

            val source = sourceType.lowercase(Locale.getDefault())
            val categorySafe = category?.ifBlank { "Miscellaneous" } ?: "Miscellaneous"
            val now = System.currentTimeMillis()
            val userUpi = currentUser.value?.upiId ?: "me@udhaarpay"

            val debitSummary = when (source) {
                "bank" -> handleBankDebit(sourceId, amount, enteredPin)
                "card" -> handleCardDebit(sourceId, amount, enteredPin)
                "wallet" -> handleWalletDebit(sourceId, amount, enteredPin)
                else -> PaymentDebitResult.error("Invalid payment source.")
            }

            if (!debitSummary.success) {
                _statusMessage.value = debitSummary.message
                return@launch
            }

            if (isSelfTransfer) {
                val wallet = walletAccount.value
                if (wallet == null) {
                    _statusMessage.value = "Wallet account is missing."
                    return@launch
                }
                bankAccountRepository.update(wallet.copy(balance = wallet.balance + amount))
            }

            upiPaymentRepository.insert(
                UPIPayment(
                    senderUPI = userUpi,
                    recipientUPI = if (isSelfTransfer) "self@udhaarpay" else recipientUpi,
                    amount = amount,
                    date = now,
                    message = note?.ifBlank { null } ?: categorySafe,
                    status = "Success",
                    type = if (isSelfTransfer) "transfer" else "sent"
                )
            )

            if (!isSelfTransfer) {
                expenseRepository.insert(
                    Expense(
                        amount = amount,
                        category = categorySafe,
                        subCategory = source.replaceFirstChar { it.uppercase(Locale.getDefault()) },
                        account = source,
                        accountName = debitSummary.accountName,
                        description = note?.ifBlank { null } ?: "Paid to $recipientName",
                        date = now,
                        month = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(now)),
                        receiptUrl = null,
                        accountId = debitSummary.accountId
                    )
                )
            }

            val debitCreditWord = if (isSelfTransfer) "transferred" else "debited"
            val timeText = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(now))
            InAppBannerManager.show(
                InAppBanner(
                    title = if (isSelfTransfer) "Self Transfer Successful" else "Payment Successful",
                    message = "INR ${"%.2f".format(amount)} $debitCreditWord from ${debitSummary.accountName} at $timeText",
                    isCredit = isSelfTransfer
                )
            )

            _statusMessage.value = if (isSelfTransfer) {
                "Self transfer completed."
            } else {
                "Payment sent successfully."
            }
        }
    }

    private suspend fun handleBankDebit(
        sourceId: Long?,
        amount: Double,
        enteredPin: String?
    ): PaymentDebitResult {
        val bank = bankAccounts.value.firstOrNull { it.accountId == sourceId }
            ?: return PaymentDebitResult.error("Select bank account.")
        val requiredPin = currentUser.value?.tpin?.takeIf { it.isNotBlank() } ?: bank.upiPin
        if (requiredPin.isNullOrBlank()) return PaymentDebitResult.error("Set TPIN for bank transfers first.")
        if (enteredPin != requiredPin) return PaymentDebitResult.error("Incorrect TPIN.")
        if (bank.balance < amount) return PaymentDebitResult.error("Insufficient bank balance.")
        bankAccountRepository.update(bank.copy(balance = bank.balance - amount))
        return PaymentDebitResult.success(accountName = bank.bankName, accountId = bank.accountId)
    }

    private suspend fun handleCardDebit(
        sourceId: Long?,
        amount: Double,
        enteredPin: String?
    ): PaymentDebitResult {
        val card = creditCards.value.firstOrNull { it.cardId == sourceId }
            ?: return PaymentDebitResult.error("Select credit card.")
        if (!card.cardType.equals("rupay", true)) {
            return PaymentDebitResult.error("Only RuPay cards are allowed for UPI/card payments.")
        }
        if (!isAnyBankPinValid(enteredPin)) {
            return PaymentDebitResult.error("UPI PIN required for card payment.")
        }
        if (card.balance < amount) return PaymentDebitResult.error("Insufficient card available balance.")
        creditCardRepository.update(card.copy(balance = card.balance - amount))
        return PaymentDebitResult.success(accountName = "${card.issuer} ****${card.cardNumber}", accountId = card.cardId)
    }

    private suspend fun handleWalletDebit(
        sourceId: Long?,
        amount: Double,
        enteredPin: String?
    ): PaymentDebitResult {
        val wallet = walletAccount.value
            ?: return PaymentDebitResult.error("Wallet account not available.")
        if (sourceId != null && sourceId != wallet.accountId) {
            return PaymentDebitResult.error("Selected wallet is invalid.")
        }
        if (wallet.balance < amount) return PaymentDebitResult.error("Insufficient wallet balance.")

        val noPinLimit = currentUser.value?.walletPinFreeLimit ?: 200.0
        if (amount > noPinLimit) {
            val pinValid = when {
                !wallet.upiPin.isNullOrBlank() -> enteredPin == wallet.upiPin
                else -> isAnyBankPinValid(enteredPin)
            }
            if (!pinValid) return PaymentDebitResult.error("UPI PIN required above INR ${"%.0f".format(noPinLimit)}.")
        }

        bankAccountRepository.update(wallet.copy(balance = wallet.balance - amount))
        return PaymentDebitResult.success(accountName = wallet.bankName, accountId = wallet.accountId)
    }

    private fun isAnyBankPinValid(pin: String?): Boolean {
        if (pin.isNullOrBlank()) return false
        return bankAccounts.value.any { !it.upiPin.isNullOrBlank() && it.upiPin == pin }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }
}

private data class PaymentDebitResult(
    val success: Boolean,
    val message: String?,
    val accountName: String,
    val accountId: Long?
) {
    companion object {
        fun success(accountName: String, accountId: Long?): PaymentDebitResult {
            return PaymentDebitResult(
                success = true,
                message = null,
                accountName = accountName,
                accountId = accountId
            )
        }

        fun error(message: String): PaymentDebitResult {
            return PaymentDebitResult(
                success = false,
                message = message,
                accountName = "",
                accountId = null
            )
        }
    }
}
