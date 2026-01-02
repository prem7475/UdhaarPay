package com.example.udhaarpay.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class EncryptionManagerTest {

    private lateinit var context: Context
    private lateinit var encryptionManager: EncryptionManager

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        encryptionManager = EncryptionManager(context)
    }

    @Test
    fun `saveWalletBalance encrypts and saves balance correctly`() {
        // Given
        val balance = 1500.0

        // When
        encryptionManager.saveWalletBalance(balance)

        // Then
        val retrievedBalance = encryptionManager.getWalletBalance()
        assertEquals(balance, retrievedBalance, 0.0)
    }

    @Test
    fun `getWalletBalance returns correct decrypted balance`() {
        // Given
        val balance = 2500.0
        encryptionManager.saveWalletBalance(balance)

        // When
        val retrievedBalance = encryptionManager.getWalletBalance()

        // Then
        assertEquals(balance, retrievedBalance, 0.0)
    }

    @Test
    fun `getWalletBalance returns zero for no saved balance`() {
        // Given - no balance saved

        // When
        val retrievedBalance = encryptionManager.getWalletBalance()

        // Then
        assertEquals(0.0, retrievedBalance, 0.0)
    }

    @Test
    fun `saveUserPin encrypts and saves PIN correctly`() {
        // Given
        val pin = "1234"

        // When
        encryptionManager.saveUserPin(pin)

        // Then
        val retrievedPin = encryptionManager.getUserPin()
        assertEquals(pin, retrievedPin)
    }

    @Test
    fun `getUserPin returns correct decrypted PIN`() {
        // Given
        val pin = "5678"
        encryptionManager.saveUserPin(pin)

        // When
        val retrievedPin = encryptionManager.getUserPin()

        // Then
        assertEquals(pin, retrievedPin)
    }

    @Test
    fun `getUserPin returns null for no saved PIN`() {
        // Given - no PIN saved

        // When
        val retrievedPin = encryptionManager.getUserPin()

        // Then
        assertNull(retrievedPin)
    }

    @Test
    fun `saveSessionToken encrypts and saves token correctly`() {
        // Given
        val token = "session_token_12345"

        // When
        encryptionManager.saveSessionToken(token)

        // Then
        val retrievedToken = encryptionManager.getSessionToken()
        assertEquals(token, retrievedToken)
    }

    @Test
    fun `clearSessionToken removes token correctly`() {
        // Given
        val token = "session_token_12345"
        encryptionManager.saveSessionToken(token)

        // When
        encryptionManager.clearSessionToken()

        // Then
        val retrievedToken = encryptionManager.getSessionToken()
        assertNull(retrievedToken)
    }

    @Test
    fun `clearAllSensitiveData removes all data correctly`() {
        // Given
        encryptionManager.saveWalletBalance(1000.0)
        encryptionManager.saveUserPin("1234")
        encryptionManager.saveSessionToken("token")

        // When
        encryptionManager.clearAllSensitiveData()

        // Then
        assertEquals(0.0, encryptionManager.getWalletBalance(), 0.0)
        assertNull(encryptionManager.getUserPin())
        assertNull(encryptionManager.getSessionToken())
    }

    @Test
    fun `encryptData returns encrypted data and IV`() {
        // Given
        val data = "sensitive_data"

        // When
        val (encryptedData, iv) = encryptionManager.encryptData(data)

        // Then
        assertNotNull(encryptedData)
        assertNotNull(iv)
        assert(encryptedData.isNotEmpty())
        assert(iv.isNotEmpty())
    }

    @Test
    fun `decryptData correctly decrypts encrypted data`() {
        // Given
        val originalData = "sensitive_information"
        val (encryptedData, iv) = encryptionManager.encryptData(originalData)

        // When
        val decryptedData = encryptionManager.decryptData(encryptedData, iv)

        // Then
        assertEquals(originalData, decryptedData)
    }
}
