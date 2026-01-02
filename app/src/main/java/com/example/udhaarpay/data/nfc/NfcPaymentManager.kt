package com.example.udhaarpay.data.nfc

import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.udhaarpay.data.security.EncryptionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NfcPaymentManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptionManager: EncryptionManager
) {

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

    private val _nfcStatus = MutableLiveData<NfcStatus>()
    val nfcStatus: LiveData<NfcStatus> = _nfcStatus

    private val _paymentResult = MutableLiveData<PaymentResult>()
    val paymentResult: LiveData<PaymentResult> = _paymentResult

    companion object {
        private const val TAG = "NfcPaymentManager"
        private const val MAX_PAYMENT_AMOUNT = 10000.0 // ₹10,000 max for NFC
        private const val NFC_TIMEOUT = 30000L // 30 seconds
    }

    init {
        checkNfcAvailability()
    }

    /**
     * Check if NFC is available and enabled on the device
     */
    fun checkNfcAvailability(): Boolean {
        return when {
            nfcAdapter == null -> {
                _nfcStatus.value = NfcStatus.Unavailable("NFC not supported on this device")
                false
            }
            !nfcAdapter.isEnabled -> {
                _nfcStatus.value = NfcStatus.Disabled("NFC is disabled. Please enable it in settings")
                false
            }
            else -> {
                _nfcStatus.value = NfcStatus.Ready("NFC is ready for payments")
                true
            }
        }
    }

    /**
     * Process NFC payment with encrypted payment data
     */
    fun processNfcPayment(amount: Double, merchantId: String): Boolean {
        if (!checkNfcAvailability()) {
            _paymentResult.value = PaymentResult.Failure("NFC not available")
            return false
        }

        if (amount > MAX_PAYMENT_AMOUNT) {
            _paymentResult.value = PaymentResult.Failure("Amount exceeds NFC payment limit (₹$MAX_PAYMENT_AMOUNT)")
            return false
        }

        // Check wallet balance
        val currentBalance = encryptionManager.getWalletBalance()
        if (currentBalance < amount) {
            _paymentResult.value = PaymentResult.Failure("Insufficient wallet balance")
            return false
        }

        // Encrypt payment data
        val paymentData = createPaymentData(amount, merchantId)
        val (encryptedData, iv) = encryptionManager.encryptData(paymentData)

        Timber.d(TAG, "NFC payment initiated: amount=$amount, merchant=$merchantId")

        // In a real implementation, this would communicate with the NFC terminal
        // For demo purposes, we'll simulate the payment process
        simulateNfcPayment(encryptedData, iv, amount, merchantId)

        return true
    }

    /**
     * Create payment data string for encryption
     */
    private fun createPaymentData(amount: Double, merchantId: String): String {
        val timestamp = System.currentTimeMillis()
        return "PAYMENT|$amount|$merchantId|$timestamp|UDHAARPAY"
    }

    /**
     * Simulate NFC payment process (in real app, this would use actual NFC communication)
     */
    private fun simulateNfcPayment(encryptedData: ByteArray, iv: ByteArray, amount: Double, merchantId: String) {
        // Simulate NFC communication delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            try {
                // Decrypt and verify payment data
                val decryptedData = encryptionManager.decryptData(encryptedData, iv)
                val parts = decryptedData.split("|")

                if (parts.size >= 5 && parts[0] == "PAYMENT") {
                    val paymentAmount = parts[1].toDoubleOrNull()
                    val paymentMerchant = parts[2]

                    if (paymentAmount == amount && paymentMerchant == merchantId) {
                        // Deduct from wallet
                        val currentBalance = encryptionManager.getWalletBalance()
                        val newBalance = currentBalance - amount
                        encryptionManager.saveWalletBalance(newBalance)

                        // Record transaction (would be done via repository in real implementation)
                        Timber.d(TAG, "NFC payment successful: amount=$amount, new balance=$newBalance")

                        _paymentResult.value = PaymentResult.Success(
                            amount = amount,
                            merchantId = merchantId,
                            transactionId = "NFC_${System.currentTimeMillis()}"
                        )
                    } else {
                        _paymentResult.value = PaymentResult.Failure("Payment data verification failed")
                    }
                } else {
                    _paymentResult.value = PaymentResult.Failure("Invalid payment data format")
                }
            } catch (e: Exception) {
                Timber.e(TAG, "NFC payment failed", e)
                _paymentResult.value = PaymentResult.Failure("Payment processing error: ${e.message}")
            }
        }, 2000) // 2 second delay to simulate NFC processing
    }

    /**
     * Handle NFC tag discovery (called from Activity's onNewIntent)
     */
    fun handleNfcTag(tag: Tag?, amount: Double, merchantId: String) {
        if (tag == null) {
            _paymentResult.value = PaymentResult.Failure("No NFC tag detected")
            return
        }

        Timber.d(TAG, "NFC tag detected: ${tag.toString()}")

        // In a real implementation, you would:
        // 1. Check tag technology (IsoDep for contactless cards)
        // 2. Establish connection
        // 3. Send payment APDUs
        // 4. Receive and process response

        // For demo, simulate successful tag detection
        processNfcPayment(amount, merchantId)
    }

    /**
     * Enable NFC foreground dispatch for payment activities
     */
    fun enableNfcForegroundDispatch(activity: androidx.fragment.app.FragmentActivity, pendingIntent: android.app.PendingIntent) {
        if (nfcAdapter?.isEnabled == true) {
            val techList = arrayOf(
                arrayOf(android.nfc.tech.IsoDep::class.java.name),
                arrayOf(android.nfc.tech.NfcA::class.java.name)
            )

            val filter = android.content.IntentFilter(android.nfc.NfcAdapter.ACTION_TAG_DISCOVERED).apply {
                addDataType("*/*")
            }

            val filters = arrayOf(filter)
            nfcAdapter.enableForegroundDispatch(activity, pendingIntent, filters, techList)
        }
    }

    /**
     * Disable NFC foreground dispatch
     */
    fun disableNfcForegroundDispatch(activity: androidx.fragment.app.FragmentActivity) {
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    /**
     * Get NFC adapter for activity setup
     */
    fun getNfcAdapter(): NfcAdapter? = nfcAdapter

    /**
     * Check if device supports HCE (Host Card Emulation) for digital wallet
     */
    fun supportsHostCardEmulation(): Boolean {
        return context.packageManager.hasSystemFeature("android.hardware.nfc.hce")
    }

    // NFC Status sealed class
    sealed class NfcStatus {
        data class Ready(val message: String) : NfcStatus()
        data class Disabled(val message: String) : NfcStatus()
        data class Unavailable(val message: String) : NfcStatus()
    }

    // Payment Result sealed class
    sealed class PaymentResult {
        data class Success(
            val amount: Double,
            val merchantId: String,
            val transactionId: String
        ) : PaymentResult()

        data class Failure(val reason: String) : PaymentResult()
    }
}
