package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.repository.*
import com.example.udhaarpay.ui.screens.scanpay.ScanPayStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ScanPayViewModel @Inject constructor(
    private val qrRepository: QRRepository,
    private val transactionRepository: TransactionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentStep = MutableStateFlow(ScanPayStep.SCANNER)
    val currentStep: StateFlow<ScanPayStep> = _currentStep.asStateFlow()

    private val _qrData = MutableStateFlow("")
    val qrData: StateFlow<String> = _qrData.asStateFlow()

    private val _recipientName = MutableStateFlow("")
    val recipientName: StateFlow<String> = _recipientName.asStateFlow()

    private val _recipientUPI = MutableStateFlow("")
    val recipientUPI: StateFlow<String> = _recipientUPI.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _remarks = MutableStateFlow("")
    val remarks: StateFlow<String> = _remarks.asStateFlow()

    private val _selectedAccount = MutableStateFlow<String?>(null)
    val selectedAccount: StateFlow<String?> = _selectedAccount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun processQRCode(scannedData: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _qrData.value = scannedData

                // Decode and validate QR code
                val result = qrRepository.decodeQRCode(scannedData)
                result.onSuccess { data ->
                    _recipientUPI.value = data["upiId"] ?: ""
                    _recipientName.value = data["recipientName"] ?: "Unknown"
                    _currentStep.value = ScanPayStep.PAYMENT_DETAILS
                    _error.value = null
                    Timber.d("QR code processed: UPI=${_recipientUPI.value}")
                }.onFailure { exception ->
                    _error.value = exception.message ?: "Failed to decode QR code"
                    Timber.e(exception, "Error decoding QR code")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setAmount(newAmount: String) {
        _amount.value = newAmount
    }

    fun setRemarks(newRemarks: String) {
        _remarks.value = newRemarks
    }

    fun moveToAccountSelection() {
        _currentStep.value = ScanPayStep.ACCOUNT_SELECTION
    }

    fun selectAccount(accountId: String) {
        _selectedAccount.value = accountId
        _currentStep.value = ScanPayStep.UPI_PIN
    }

    fun processPayment(upiPin: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val fromAccount = _selectedAccount.value ?: return@launch
                val toUPI = _recipientUPI.value
                val paymentAmount = _amount.value.toDoubleOrNull() ?: return@launch
                val category = "Payment" // Default category
                val remark = _remarks.value

                val result = transactionRepository.processUPIPayment(
                    fromAccount = fromAccount,
                    toUPI = toUPI,
                    amount = paymentAmount,
                    upiPin = upiPin,
                    category = category,
                    remarks = remark
                )

                result.onSuccess { response ->
                    _currentStep.value = ScanPayStep.CONFIRMATION
                    _error.value = null
                    Timber.d("Payment processed successfully: ${response["transactionId"]}")
                }.onFailure { exception ->
                    _error.value = exception.message ?: "Payment failed"
                    Timber.e(exception, "Error processing payment")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun goBackToPaymentDetails() {
        _currentStep.value = ScanPayStep.PAYMENT_DETAILS
    }

    fun goBackToAccountSelection() {
        _currentStep.value = ScanPayStep.ACCOUNT_SELECTION
    }

    fun resetFlow() {
        _currentStep.value = ScanPayStep.SCANNER
        _qrData.value = ""
        _recipientName.value = ""
        _recipientUPI.value = ""
        _amount.value = ""
        _remarks.value = ""
        _selectedAccount.value = null
        _error.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
