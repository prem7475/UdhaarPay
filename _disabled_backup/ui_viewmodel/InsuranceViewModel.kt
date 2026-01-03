package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.InsuranceDao
import com.example.udhaarpay.data.model.Insurance
import com.example.udhaarpay.data.model.InsuranceCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsuranceViewModel @Inject constructor(
    private val insuranceDao: InsuranceDao
) : ViewModel() {

    private val _insurance = MutableStateFlow<List<Insurance>>(emptyList())
    val insurance: StateFlow<List<Insurance>> = _insurance.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var userId: Long = 0

    fun setUserId(userId: Long) {
        this.userId = userId
        loadInsurance()
    }

    fun loadInsurance() {
        viewModelScope.launch {
            insuranceDao.getUserInsurance(userId).collect { insurance ->
                _insurance.value = insurance
            }
        }
    }

    fun saveInsuranceRecord(
        category: InsuranceCategory,
        provider: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val insurance = Insurance(
                    userId = userId,
                    category = category,
                    provider = provider
                )

                insuranceDao.insertInsurance(insurance)
                loadInsurance()
            } catch (e: Exception) {
                // Log error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
