package com.example.udhaarpay.ui.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.dao.DebtDao
import com.example.udhaarpay.data.model.Debt
import com.example.udhaarpay.data.model.DebtCategory
import com.example.udhaarpay.data.model.DebtSummary
import com.example.udhaarpay.data.model.DebtType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val debtDao: DebtDao
) : ViewModel() {

    private val _allDebts = MutableStateFlow<List<Debt>>(emptyList())
    val allDebts: StateFlow<List<Debt>> = _allDebts.asStateFlow()

    private val _lentDebts = MutableStateFlow<List<Debt>>(emptyList())
    val lentDebts: StateFlow<List<Debt>> = _lentDebts.asStateFlow()

    private val _borrowedDebts = MutableStateFlow<List<Debt>>(emptyList())
    val borrowedDebts: StateFlow<List<Debt>> = _borrowedDebts.asStateFlow()

    private val _pendingDebts = MutableStateFlow<List<Debt>>(emptyList())
    val pendingDebts: StateFlow<List<Debt>> = _pendingDebts.asStateFlow()

    private val _debtSummary = MutableStateFlow(DebtSummary())
    val debtSummary: StateFlow<DebtSummary> = _debtSummary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _debtAdded = MutableStateFlow(false)
    val debtAdded: StateFlow<Boolean> = _debtAdded.asStateFlow()

    private var userId: Int = 1

    init {
        loadUserDebts()
    }

    fun setUserId(userId: Int) {
        this.userId = userId
        loadUserDebts()
    }

    fun loadUserDebts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load all debts
                debtDao.getUserDebts(userId).collect { allDebts ->
                    _allDebts.value = allDebts
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }

        // Load debts by type
        viewModelScope.launch {
            debtDao.getUserDebtsByType(userId, DebtType.LENT_TO).collect { lent ->
                _lentDebts.value = lent
            }
        }

        viewModelScope.launch {
            debtDao.getUserDebtsByType(userId, DebtType.BORROWED_FROM).collect { borrowed ->
                _borrowedDebts.value = borrowed
            }
        }

        // Load pending debts
        viewModelScope.launch {
            debtDao.getPendingDebts(userId).collect { pending ->
                _pendingDebts.value = pending
            }
        }

        // Load summary
        viewModelScope.launch {
            launch {
                debtDao.getTotalLent(userId).collect { lent ->
                    updateSummary()
                }
            }
            launch {
                debtDao.getTotalBorrowed(userId).collect { borrowed ->
                    updateSummary()
                }
            }
        }
    }

    private fun updateSummary() {
        viewModelScope.launch {
            val totalLent = debtDao.getTotalLent(userId).collect { amount ->
                val totalBorrowed = debtDao.getTotalBorrowed(userId).collect { borrowed ->
                    val lent = amount ?: 0.0
                    val borrowed = borrowed ?: 0.0
                    _debtSummary.value = DebtSummary(
                        totalLent = lent,
                        totalBorrowed = borrowed,
                        netAmount = lent - borrowed
                    )
                }
            }
        }
    }

    fun addDebt(
        personName: String,
        amount: Double,
        debtType: DebtType,
        category: DebtCategory,
        phoneNumber: String = "",
        email: String = "",
        description: String = "",
        dueDate: Long? = null,
        fromAccountId: Int? = null
    ) {
        if (personName.isBlank() || amount <= 0) {
            _error.value = "Please enter valid name and amount"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val debt = Debt(
                    userId = userId,
                    personName = personName,
                    phoneNumber = phoneNumber,
                    email = email,
                    amount = amount,
                    debtType = debtType,
                    category = category,
                    fromAccountId = fromAccountId,
                    description = description,
                    dueDate = dueDate,
                    remainingAmount = amount
                )
                debtDao.insertDebt(debt)
                _debtAdded.value = true
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun settleDebt(debtId: Int, amount: Double? = null) {
        viewModelScope.launch {
            try {
                val debt = debtDao.getDebtById(debtId)
                debt?.let {
                    val settleAmount = amount ?: it.remainingAmount
                    debtDao.settleDebt(debtId, settleAmount)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun partialSettlement(debtId: Int, amount: Double) {
        viewModelScope.launch {
            try {
                debtDao.partialSettlement(debtId, amount)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateDebt(debt: Debt) {
        viewModelScope.launch {
            try {
                debtDao.updateDebt(debt)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteDebt(debt: Debt) {
        viewModelScope.launch {
            try {
                debtDao.deleteDebt(debt)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _debtAdded.value = false
    }
}
