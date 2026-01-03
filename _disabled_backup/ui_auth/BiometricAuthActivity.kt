package com.example.udhaarpay.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.udhaarpay.MainActivity
import com.example.udhaarpay.R
import com.example.udhaarpay.databinding.ActivityBiometricAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BiometricAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBiometricAuthBinding

    // Use AndroidX BiometricManager obtained from context instead of injecting

    private val executor by lazy {
        ContextCompat.getMainExecutor(this)
    }

    private val biometricPrompt by lazy {
        BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Timber.e("Biometric authentication error: $errorCode - $errString")
                Toast.makeText(this@BiometricAuthActivity, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Timber.d("Biometric authentication succeeded")
                Toast.makeText(this@BiometricAuthActivity, "Authentication successful!", Toast.LENGTH_SHORT).show()

                // Navigate to main activity
                startActivity(Intent(this@BiometricAuthActivity, MainActivity::class.java))
                finish()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Timber.w("Biometric authentication failed")
                Toast.makeText(this@BiometricAuthActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val promptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("UdhaarPay Authentication")
            .setSubtitle("Confirm your identity")
            .setDescription("Use your fingerprint or face to unlock UdhaarPay")
            .setNegativeButtonText("Use PIN")
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        checkBiometricAvailability()
    }

    private fun setupUI() {
        binding.btnAuthenticate.setOnClickListener {
            authenticateWithBiometrics()
        }

        binding.btnUsePin.setOnClickListener {
            // Navigate to PIN authentication
            startActivity(Intent(this, PinAuthActivity::class.java))
            finish()
        }

        binding.btnSkip.setOnClickListener {
            // Skip authentication (for demo purposes)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun checkBiometricAvailability() {
        val bm = BiometricManager.from(this@BiometricAuthActivity)
        @Suppress("DEPRECATION")
        when (bm.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                binding.tvBiometricStatus.text = "Biometric authentication available"
                binding.btnAuthenticate.isEnabled = true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                binding.tvBiometricStatus.text = "No biometric hardware available"
                binding.btnAuthenticate.isEnabled = false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                binding.tvBiometricStatus.text = "Biometric hardware unavailable"
                binding.btnAuthenticate.isEnabled = false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.tvBiometricStatus.text = "No biometric credentials enrolled"
                binding.btnAuthenticate.isEnabled = false
            }
            else -> {
                binding.tvBiometricStatus.text = "Biometric unavailable"
                binding.btnAuthenticate.isEnabled = false
            }
        }
    }

    private fun authenticateWithBiometrics() {
        biometricPrompt.authenticate(promptInfo)
    }

    override fun onResume() {
        super.onResume()
        // Auto-start authentication if biometrics are available
        val bm = BiometricManager.from(this@BiometricAuthActivity)
        @Suppress("DEPRECATION")
        if (bm.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            authenticateWithBiometrics()
        }
    }
}
