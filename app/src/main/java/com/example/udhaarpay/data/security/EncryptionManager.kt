package com.example.udhaarpay.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val WALLET_BALANCE_KEY = "encrypted_wallet_balance"
        private const val USER_PIN_KEY = "encrypted_user_pin"
        private const val SESSION_TOKEN_KEY = "encrypted_session_token"
    }

    // Secure SharedPreferences operations
    fun saveWalletBalance(balance: Double) {
        encryptedSharedPreferences.edit()
            .putString(WALLET_BALANCE_KEY, balance.toString())
            .apply()
    }

    fun getWalletBalance(): Double {
        val balanceStr = encryptedSharedPreferences.getString(WALLET_BALANCE_KEY, "0.0") ?: "0.0"
        return balanceStr.toDoubleOrNull() ?: 0.0
    }

    fun saveUserPin(pin: String) {
        encryptedSharedPreferences.edit()
            .putString(USER_PIN_KEY, pin)
            .apply()
    }

    fun getUserPin(): String? {
        return encryptedSharedPreferences.getString(USER_PIN_KEY, null)
    }

    fun saveSessionToken(token: String) {
        encryptedSharedPreferences.edit()
            .putString(SESSION_TOKEN_KEY, token)
            .apply()
    }

    fun getSessionToken(): String? {
        return encryptedSharedPreferences.getString(SESSION_TOKEN_KEY, null)
    }

    fun clearSessionToken() {
        encryptedSharedPreferences.edit()
            .remove(SESSION_TOKEN_KEY)
            .apply()
    }

    fun clearAllSensitiveData() {
        encryptedSharedPreferences.edit()
            .clear()
            .apply()
    }

    // Advanced encryption for additional security (using Android Keystore)
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = java.security.KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val key = keyStore.getKey("udhaar_pay_key", null)
        if (key != null) {
            return key as SecretKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            "udhaar_pay_key",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    fun encryptData(data: String): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())

        val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        val iv = cipher.iv

        return Pair(encryptedData, iv)
    }

    fun decryptData(encryptedData: ByteArray, iv: ByteArray): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)

        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData, Charsets.UTF_8)
    }
}
