package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.BankAccountDao
import com.example.udhaarpay.data.local.dao.ExpenseDao
import com.example.udhaarpay.data.local.dao.UserDao
import com.example.udhaarpay.data.local.entity.BankAccountEntity
import com.example.udhaarpay.data.local.entity.ExpenseEntity
import com.example.udhaarpay.data.local.entity.WalletEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExpenseState(
    val bankAccounts: List<BankAccountEntity> = emptyList(),
    val walletBalance: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val userDao: UserDao,
    private val bankAccountDao: BankAccountDao
) : ViewModel() {

    // Combine wallet and bank data for UI state
    val state: StateFlow<ExpenseState> = combine(
        userDao.getWallet(),
        bankAccountDao.getAllBankAccounts()
    ) { wallet, banks ->
        ExpenseState(
            bankAccounts = banks,
            walletBalance = wallet?.currentBalance ?: 0.0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExpenseState())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun addExpense(amount: Double, category: String, source: String, description: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Record the expense
                val expense = ExpenseEntity(
                    amount = amount,
                    category = category,
                    description = description,
                    source = source,
                    date = System.currentTimeMillis()
                )
                expenseDao.insertExpense(expense)

                // 2. Deduct from Wallet if applicable
                if (source == "Cash/Wallet") {
                    val currentWallet = userDao.getWallet().first()
                    val currentBalance = currentWallet?.currentBalance ?: 0.0
                    val newBalance = currentBalance - amount
                    
                    // Update or insert wallet
                    val walletEntity = currentWallet?.copy(currentBalance = newBalance) 
                        ?: WalletEntity(id = 1, currentBalance = newBalance)
                    
                    userDao.insertOrUpdateWallet(walletEntity)
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateWalletBalance(newBalance: Double) {
        viewModelScope.launch {
            val wallet = WalletEntity(id = 1, currentBalance = newBalance)
            userDao.insertOrUpdateWallet(wallet)
        }
    }
}
