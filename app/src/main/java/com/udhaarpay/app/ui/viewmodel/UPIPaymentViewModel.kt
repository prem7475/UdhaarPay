package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.data.local.dao.UPIPaymentDao
import com.udhaarpay.app.data.local.entities.UPIPayment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UPIPaymentViewModel @Inject constructor(
    private val upiPaymentDao: UPIPaymentDao
) : ViewModel() {
    val payments: StateFlow<List<UPIPayment>> =
        upiPaymentDao.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(payment: UPIPayment) {
        viewModelScope.launch { upiPaymentDao.insert(payment) }
    }

    fun delete(payment: UPIPayment) {
        viewModelScope.launch { upiPaymentDao.delete(payment) }
    }

    fun update(payment: UPIPayment) {
        viewModelScope.launch { upiPaymentDao.update(payment) }
    }
}
