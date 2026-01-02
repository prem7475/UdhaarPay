package com.example.udhaarpay.domain.service

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.udhaarpay.utils.Constants
import com.example.udhaarpay.utils.ErrorHandler
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityService @Inject constructor(
    private val context: Context,
    private val errorHandler: ErrorHandler
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        Constants.KEY_ENCRYPTED_PREFS,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Encrypt and store UPI PIN
    fun storeUpiPin(userId: String, pin: String): Boolean {
        return try {
            val encryptedPin = encryptData(pin)
            encryptedPrefs.edit()
                .putString("${Constants.KEY_UPI_PIN}_$userId", encryptedPin)
                .apply()
            true
        } catch (e: Exception) {
            errorHandler.handleError(e, "SecurityService.storeUpiPin")
            false
        }
    }

    // Verify UPI PIN
    fun verifyUpiPin(userId: String, pin: String): Boolean {
        return try {
            val storedEncryptedPin = encryptedPrefs.getString("${Constants.KEY_UPI_PIN}_$userId", null)
                ?: return false

            val decryptedPin = decryptData(storedEncryptedPin)
            decryptedPin == pin
        } catch (e: Exception) {
            errorHandler.handleError(e, "SecurityService.verifyUpiPin")
            false
        }
    }

    // Change UPI PIN
    fun changeUpiPin(userId: String, oldPin: String, newPin: String): Boolean {
        return try {
            if (!verifyUpiPin(userId, oldPin)) {
                return false
            }

            storeUpiPin(userId, newPin)
            true
        } catch (e: Exception) {
            errorHandler.handleError(e, "SecurityService.changeUpiPin")
            false
        }
    }

    // Store biometric authentication preference
    fun setBiometricEnabled(userId: String, enabled: Boolean) {
        try {
            encryptedPrefs.edit()
                .putBoolean("${Constants.KEY_BIOMETRIC_ENABLED}_$userId", enabled)
                .apply()
        } catch (e: Exception) {
            errorHandler.handleError(e, "SecurityService.setBiometricEnabled")
        }
    }

    // Check if biometric is enabled
    fun isBiometricEnabled(userId: String): Boolean {
        return try {
            encryptedPrefs.getBoolean("${Constants.KEY_BIOMETRIC_ENABLED}_$userId", false)
        } catch (e: Exception) {
            errorHandler.handleError(e, "SecurityService.isBiometricEnabled")
            false
        }
    }

    // Store authentication token securely
    fun storeAuthToken(userId: String, token: String) {
        try {
            val encryptedToken = encryptData(token)
            encryptedPrefs.edit()
                .putString("${Constants.KEY_AUTH_TOKEN}_$userId", encryptedToken)
                .apply()
        } catch (e: Exception) {
            errorHandler.handleError(e, "SecurityService.storeAuthToken")
        }
    }

    // Get authentication token
    fun getAuthToken(userId: String): String? {
        return try {
            val encryptedToken = encryptedPrefs.getString("${Constants.KEY_AUTH_TOKEN}_$userId", null)
                ?: return null

            decryptData(encryptedToken)
        } catch (e: Exception) {
            errorHandler.handleError(e, "SecurityService.getAuthToken")
            null
        }
    }

    // Clear all user data (logout)
    fun clearUserData(userId: String) {
        try {
            encryptedPrefs.edit()
                .remove("${Constants.KEY_UPI_PIN}_$userId")
                .remove("${Constants.KEY_BIOMETRIC_ENABLED}_$userId")
                .remove("${Constants.KEY_AUTH_TOKEN}_$userId")
                .apply()
        } catch (e: Exception) {
            errorHandler.handleError(e, "SecurityService.clearUserData")
        }
    }

    // Generate secure transaction key
    fun generateTransactionKey(): String {
        return try {
            val key = generateSecretKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, key)

            val iv = cipher.iv
            val encryptedData = cipher.doFinal("transaction_key".toByteArray())

            // Combine IV and encrypted data
            val combined = ByteArray(iv.size + encryptedData.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedData, 0, combined, iv.size, encryptedData.size)

            android.util.Base64.encodeToString(combined, android.util.Base64.DEFAULT)
        } catch (e: Exception) {
            errorHandler.handleError(e, "SecurityService.generateTransactionKey")
            ""
        }
    }

    // Encrypt sensitive data
    private fun encryptData(data: String): String {
        return try {
            val key = generateSecretKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, key)

            val iv = cipher.iv
            val encryptedData = cipher.doFinal(data.toByteArray())

            // Combine IV and encrypted data
            val combined = ByteArray(iv.size + encryptedData.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedData, 0, combined, iv.size, encryptedData.size)

            android.util.Base64.encodeToString(combined, android.util.Base64.DEFAULT)
        } catch (e: Exception) {
            errorHandler.handleError(e, "SecurityService.encryptData")
            ""
        }
    }

    // Decrypt sensitive data
    private fun decryptData(encryptedData: String): String {
        return try {
            val key = generateSecretKey()
            val decodedData = android.util.Base64.decode(encryptedData, android.util.Base64.DEFAULT)

            // Extract IV and encrypted data
            val iv = decodedData.copyOfRange(0, 12)
            val encrypted = decodedData.copyOfRange(12, decodedData.size)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)

            val decryptedData = cipher.doFinal(encrypted)
            String(decryptedData)
        } catch (e: Exception) {
            errorHandler.handleError(e, "SecurityService.decryptData")
            ""
        }
    }

    // Generate secret key for encryption
    private fun generateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val keyAlias = "udhaarpay_key"

        // Check if key already exists
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }

        return keyStore.getKey(keyAlias, null) as SecretKey
    }

    // Hash sensitive data for additional security
    fun hashData(data: String): String {
        return try {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(data.toByteArray())
            android.util.Base64.encodeToString(hash, android.util.Base64.DEFAULT)
        } catch (e: Exception) {
            errorHandler.handleError(e, "SecurityService.hashData")
            ""
        }
    }

    // Validate PIN strength
    fun isPinStrong(pin: String): Boolean {
        return pin.length >= Constants.MIN_UPI_PIN_LENGTH &&
               pin.length <= Constants.MAX_UPI_PIN_LENGTH &&
               pin.any { it.isDigit() } &&
               pin.any { it.isLetter() }
    }
}
