package com.udhaarpay.app.ui.screens.bankaccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.data.local.dao.BankAccountDao
import com.udhaarpay.app.data.local.entities.BankAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BankAccountViewModel @Inject constructor(
    private val bankAccountDao: BankAccountDao
) : ViewModel() {
    val accounts: StateFlow<List<BankAccount>> =
        bankAccountDao.getAllBankAccounts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addBankAccount(account: BankAccount) {
        viewModelScope.launch {
            bankAccountDao.insertBankAccount(account)
        }
    }

    fun deleteBankAccount(account: BankAccount) {
        viewModelScope.launch {
            bankAccountDao.deleteBankAccount(account)
        }
    }
}