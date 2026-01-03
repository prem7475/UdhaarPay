package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.MobileRechargeDao
import com.example.udhaarpay.data.model.MobileRecharge
import com.example.udhaarpay.data.model.RechargeOperator
import com.example.udhaarpay.data.model.TransactionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RechargeViewModel @Inject constructor(
    private val rechargeDao: MobileRechargeDao
) : ViewModel() {

    private val _recharges = MutableStateFlow<List<MobileRecharge>>(emptyList())
    val recharges: StateFlow<List<MobileRecharge>> = _recharges.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    private var userId: Long = 0

    fun setUserId(userId: Long) {
        this.userId = userId
        loadRecharges()
    }

    fun loadRecharges() {
        viewModelScope.launch {
            rechargeDao.getUserRecharges(userId).collect { recharges ->
                _recharges.value = recharges
            }
        }
    }

    fun saveRecharge(
        phoneNumber: String,
        operator: RechargeOperator,
        planAmount: Double,
        planId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val recharge = MobileRecharge(
                    userId = userId,
                    phoneNumber = phoneNumber,
                    operator = operator,
                    planAmount = planAmount,
                    planId = planId,
                    status = TransactionStatus.SUCCESS
                )

                rechargeDao.insertRecharge(recharge)
                _success.value = "Recharge request saved for $phoneNumber"
                _error.value = null
                loadRecharges()
            } catch (e: Exception) {
                _error.value = "Failed to save recharge: ${e.message}"
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
