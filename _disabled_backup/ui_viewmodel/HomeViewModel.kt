package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.BankAccountDao
import com.example.udhaarpay.data.local.dao.CreditCardDao
import com.example.udhaarpay.data.local.dao.TransactionDao
import com.example.udhaarpay.data.local.entity.TransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val totalBalance: String = "0.0",
    val creditLimit: String = "0.0",
    val transactions: List<TransactionEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bankAccountDao: BankAccountDao,
    private val creditCardDao: CreditCardDao,
    private val transactionDao: TransactionDao
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            try {
                // Combine flows from DAOs
                combine(
                    bankAccountDao.getAllBankAccounts(),
                    creditCardDao.getAllCreditCards(),
                    transactionDao.getAllTransactions()
                ) { bankAccounts, creditCards, transactions ->
                    val totalBalance = bankAccounts.sumOf { it.balance }
                    val totalLimit = creditCards.sumOf { it.limit }
                    
                    HomeState(
                        totalBalance = String.format("%.2f", totalBalance),
                        creditLimit = String.format("%.2f", totalLimit),
                        transactions = transactions.take(10), // Show latest 10
                        isLoading = false
                    )
                }.collect { newState ->
                    _state.value = newState
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
