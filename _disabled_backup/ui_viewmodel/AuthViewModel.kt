package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.model.User
import com.example.udhaarpay.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _otpSent = MutableStateFlow(false)
    val otpSent: StateFlow<Boolean> = _otpSent.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _authSuccess = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess.asStateFlow()

    fun setPhoneNumber(phone: String) {
        _phoneNumber.value = phone.filter { it.isDigit() }.take(10)
    }

    fun setOTP(code: String) {
        _otp.value = code.filter { it.isDigit() }.take(6)
    }

    fun sendOTP() {
        val phone = _phoneNumber.value
        if (phone.length != 10) {
            _error.value = "Please enter a valid 10-digit phone number"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val fullPhone = "+91$phone"
            val result = authRepository.sendOTP(fullPhone)

            result.onSuccess {
                _otpSent.value = true
                _error.value = null
                Timber.d("OTP sent successfully to $fullPhone")
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to send OTP"
                _otpSent.value = false
                Timber.e(exception, "Error sending OTP")
            }

            _isLoading.value = false
        }
    }

    fun verifyOTP() {
        val phone = _phoneNumber.value
        val otp = _otp.value

        if (phone.length != 10) {
            _error.value = "Invalid phone number"
            return
        }

        if (otp.length != 6) {
            _error.value = "Please enter a valid 6-digit OTP"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val fullPhone = "+91$phone"
            val result = authRepository.verifyOTP(fullPhone, otp)

            result.onSuccess { user ->
                _currentUser.value = user
                _authSuccess.value = true
                _error.value = null
                Timber.d("User verified successfully: ${user.phoneNumber}")
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to verify OTP"
                _authSuccess.value = false
                Timber.e(exception, "Error verifying OTP")
            }

            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetAuth() {
        _phoneNumber.value = ""
        _otp.value = ""
        _otpSent.value = false
        _error.value = null
        _authSuccess.value = false
    }

    fun checkCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _currentUser.value = user
            if (user != null) {
                _authSuccess.value = true
            }
        }
    }
}
