package com.example.udhaarpay.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.model.BankAccount
import com.example.udhaarpay.data.repository.BankAccountRepository
import com.example.udhaarpay.data.UserRepository
import kotlinx.coroutines.launch

class SharedViewModel(
    application: Application,
    private val bankAccountRepository: BankAccountRepository?,
    private val userRepository: UserRepository?
) : AndroidViewModel(application) {

    // Replace readAllData with getBankAccountsForUser (example userId: 0)
    val bankAccounts: LiveData<List<BankAccount>> = MutableLiveData<List<BankAccount>>()

    private val _creditCards = MutableLiveData<List<String>>(emptyList())
    val creditCards: LiveData<List<String>> = _creditCards

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    init {
        _userName.value = userRepository?.getUserName() ?: "User"
        _userEmail.value = userRepository?.getUserEmail() ?: ""
    }

    fun addBankAccount(bankAccount: BankAccount) {
        viewModelScope.launch {
            bankAccountRepository?.addBankAccount(bankAccount)
        }
    }

    fun deleteBankAccount(bankAccount: BankAccount) {
        viewModelScope.launch {
            bankAccountRepository?.deleteBankAccount(bankAccount)
        }
    }

    fun addCreditCard(card: String) {
        val currentList = _creditCards.value ?: emptyList()
        _creditCards.value = currentList + card
    }

    fun setUserName(name: String) {
        userRepository?.saveUserName(name)
        _userName.value = name
    }

    fun setUserEmail(email: String) {
        userRepository?.saveUserEmail(email)
        _userEmail.value = email
    }

    fun logout() {
        userRepository?.logout()
        // Here you might also want to clear bank accounts from Room, if they shouldn't persist across logins.
        // For now, we'll just clear the ViewModel data.
        _userName.value = "User"
        _userEmail.value = ""
        _creditCards.value = emptyList()
    }
}