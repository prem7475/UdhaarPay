package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.BankAccountDao
import com.example.udhaarpay.data.local.entity.BankAccountEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class BankViewModel @Inject constructor(
    private val bankAccountDao: BankAccountDao
) : ViewModel() {

    val bankAccounts: StateFlow<List<BankAccountEntity>> = bankAccountDao.getAllBankAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)

    fun addBankAccount(bankName: String, holderName: String, accountNumber: String, ifsc: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // Mock balance between 10k and 50k
            val mockBalance = Random.nextDouble(10000.0, 50000.0)
            
            val bankAccount = BankAccountEntity(
                bankName = bankName,
                accountNumber = accountNumber,
                ifscCode = ifsc,
                balance = mockBalance,
                type = "Savings", // Defaulting to Savings for simplicity
                isPrimary = false
            )
            bankAccountDao.insertBankAccount(bankAccount)
            _isLoading.value = false
        }
    }

    fun deleteBankAccount(account: BankAccountEntity) {
        viewModelScope.launch {
            bankAccountDao.deleteBankAccount(account)
        }
    }
}
