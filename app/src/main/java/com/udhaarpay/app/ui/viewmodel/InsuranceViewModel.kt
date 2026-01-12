package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.repository.InsuranceRepository
import com.udhaarpay.app.data.local.entities.Insurance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsuranceViewModel @Inject constructor(
    private val repository: InsuranceRepository
) : ViewModel() {
    val insurances: StateFlow<List<Insurance>> =
        repository.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(insurance: Insurance) {
        viewModelScope.launch { repository.insert(insurance) }
    }

    fun delete(insurance: Insurance) {
        viewModelScope.launch { repository.delete(insurance) }
    }

    fun update(insurance: Insurance) {
        viewModelScope.launch { repository.update(insurance) }
    }
}
