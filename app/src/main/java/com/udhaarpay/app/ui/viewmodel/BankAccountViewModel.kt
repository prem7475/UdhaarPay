package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.repository.BankAccountRepository
import com.udhaarpay.app.data.local.entities.BankAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.absoluteValue
import javax.inject.Inject

@HiltViewModel
class BankAccountViewModel @Inject constructor(
    private val repository: BankAccountRepository
) : ViewModel() {
    val accounts: StateFlow<List<BankAccount>> =
        repository.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBalance: StateFlow<Double> = repository.getAll()
        .map { accounts -> accounts.sumOf { it.balance } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    private val _loadingAccountIds = MutableStateFlow<Set<Long>>(emptySet())
    val loadingAccountIds: StateFlow<Set<Long>> = _loadingAccountIds

    fun addBankAccount(account: BankAccount) {
        viewModelScope.launch {
            repository.insert(account)
            _statusMessage.value = "Bank account added"
        }
    }

    fun deleteBankAccount(account: BankAccount) {
        viewModelScope.launch {
            repository.delete(account)
            _statusMessage.value = "Account removed"
        }
    }

    fun checkBalance(account: BankAccount) {
        viewModelScope.launch {
            _loadingAccountIds.value = _loadingAccountIds.value + account.accountId
            delay(2000L)
            val refreshedBalance = generateMockBalance(account)
            repository.update(account.copy(balance = refreshedBalance))
            _loadingAccountIds.value = _loadingAccountIds.value - account.accountId
            _statusMessage.value = "Balance refreshed for ${account.bankName}"
        }
    }

    fun checkAllBalances() {
        viewModelScope.launch {
            accounts.value.forEach { account ->
                _loadingAccountIds.value = _loadingAccountIds.value + account.accountId
            }
            delay(2000L)
            accounts.value.forEach { account ->
                val refreshedBalance = generateMockBalance(account)
                repository.update(account.copy(balance = refreshedBalance))
                _loadingAccountIds.value = _loadingAccountIds.value - account.accountId
            }
            _statusMessage.value = "Balances refreshed for all linked accounts"
        }
    }

    fun updateNickname(account: BankAccount, nickname: String) {
        viewModelScope.launch {
            repository.update(account.copy(nickname = nickname.ifBlank { null }))
            _statusMessage.value = "Nickname updated"
        }
    }

    fun updateUpiPin(account: BankAccount, pin: String) {
        viewModelScope.launch {
            repository.update(account.copy(upiPin = pin))
            _statusMessage.value = "UPI PIN updated for ${account.bankName}"
        }
    }

    fun verifyUpiPin(accountId: Long, pin: String): Boolean {
        val account = accounts.value.firstOrNull { it.accountId == accountId } ?: return false
        return !account.upiPin.isNullOrBlank() && account.upiPin == pin
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun availableBanks(): List<String> = listOf(
        "State Bank of India",
        "HDFC Bank",
        "ICICI Bank",
        "Axis Bank",
        "Kotak Mahindra Bank",
        "IndusInd Bank",
        "IDBI Bank",
        "Punjab National Bank",
        "Bank of Baroda",
        "Canara Bank",
        "Union Bank of India",
        "Federal Bank",
        "Yes Bank",
        "RBL Bank",
        "Standard Chartered Bank",
        "HSBC",
        "CitiBank",
        "AU Small Finance Bank",
        "Bandhan Bank",
        "IDFC First Bank",
        "UCO Bank",
        "Indian Bank",
        "Central Bank of India",
        "South Indian Bank",
        "Karur Vysya Bank",
        "Bank of India",
        "Bank of Maharashtra",
        "Punjab and Sind Bank",
        "Indian Overseas Bank",
        "Karnataka Bank",
        "Tamilnad Mercantile Bank",
        "Dhanlaxmi Bank",
        "City Union Bank",
        "Nainital Bank",
        "Jammu and Kashmir Bank",
        "Saraswat Bank",
        "Shamrao Vithal Co-op Bank",
        "Cosmos Bank",
        "Abhyudaya Co-op Bank",
        "Airtel Payments Bank",
        "Fino Payments Bank",
        "India Post Payments Bank",
        "Jio Payments Bank",
        "NSDL Payments Bank",
        "Ujjivan Small Finance Bank",
        "Equitas Small Finance Bank",
        "ESAF Small Finance Bank",
        "Suryoday Small Finance Bank",
        "Jana Small Finance Bank",
        "Utkarsh Small Finance Bank",
        "AU Bank",
        "CSB Bank",
        "DCB Bank",
        "RBL Finserv Bank",
        "DBS Bank India",
        "Deutsche Bank India",
        "Barclays Bank India",
        "Bank of Bahrain and Kuwait",
        "Mizuho Bank",
        "MUFG Bank",
        "BNP Paribas India",
        "Societe Generale India",
        "Rabobank India",
        "NatWest Markets India",
        "Standard Chartered",
        "American Express Banking Corp",
        "Cathay United Bank",
        "Bank of Ceylon"
    )

    private fun generateMockBalance(account: BankAccount): Double {
        val hashSeed = "${account.bankName}_${account.accountNumber}_${System.currentTimeMillis() / 60000L}"
            .lowercase(Locale.getDefault())
            .hashCode()
            .absoluteValue
        return (1500 + (hashSeed % 250000)).toDouble()
    }

}
