package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.TransferDao
import com.example.udhaarpay.data.local.dao.UserDao
import com.example.udhaarpay.data.model.Transfer
import com.example.udhaarpay.data.model.TransactionStatus
import com.example.udhaarpay.data.model.TransferType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val transferDao: TransferDao,
    private val userDao: UserDao
) : ViewModel() {

    private val _transfers = MutableStateFlow<List<Transfer>>(emptyList())
    val transfers: StateFlow<List<Transfer>> = _transfers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    private var userId: Long = 0

    fun setUserId(userId: Long) {
        this.userId = userId
        loadTransfers()
    }

    fun loadTransfers() {
        viewModelScope.launch {
            transferDao.getUserTransfers(userId).collect { transfers ->
                _transfers.value = transfers
            }
        }
    }

    fun transferFunds(
        fromAccount: String,
        toAccount: String,
        amount: Double
    ) {
        if (amount <= 0) {
            _error.value = "Amount must be greater than 0"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userDao.getUserById(userId.toInt())
                if (user != null) {
                    // Check balance
                    val currentBalance = if (fromAccount == "Bank") {
                        user.bankBalance
                    } else {
                        user.walletBalance
                    }

                    if (currentBalance < amount) {
                        _error.value = "Insufficient balance in $fromAccount"
                        _isLoading.value = false
                        return@launch
                    }

                    // Create transfer record
                    val transferType = when {
                        fromAccount == "Bank" && toAccount == "Wallet" -> TransferType.BANK_TO_WALLET
                        fromAccount == "Wallet" && toAccount == "Bank" -> TransferType.WALLET_TO_BANK
                        else -> TransferType.BANK_TO_BANK
                    }

                    val transfer = Transfer(
                        userId = userId,
                        fromAccount = fromAccount,
                        toAccount = toAccount,
                        amount = amount,
                        transferType = transferType,
                        status = TransactionStatus.SUCCESS
                    )

                    transferDao.insertTransfer(transfer)

                    // Update user balances
                    val updatedUser = user.copy(
                        bankBalance = if (fromAccount == "Bank") {
                            user.bankBalance - amount
                        } else {
                            user.bankBalance + amount
                        },
                        walletBalance = if (fromAccount == "Wallet") {
                            user.walletBalance - amount
                        } else {
                            user.walletBalance + amount
                        }
                    )

                    userDao.updateUser(updatedUser)

                    _success.value = "₹$amount transferred from $fromAccount to $toAccount"
                    _error.value = null
                    loadTransfers()
                } else {
                    _error.value = "User not found"
                }
            } catch (e: Exception) {
                _error.value = "Transfer failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _success.value = null
    }
}
