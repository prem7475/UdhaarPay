package com.example.udhaarpay

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.udhaarpay.domain.service.SecurityService
import com.example.udhaarpay.utils.ErrorHandler
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurityIntegrationTest {

    private lateinit var securityService: SecurityService
    private val testUserId = "test_user_security"
    private val testUpiPin = "1234"
    private val newUpiPin = "5678"

    @Before
    fun setup() {
        securityService = SecurityService(
            ApplicationProvider.getApplicationContext(),
            ErrorHandler()
        )
    }

    @Test
    fun testUpiPinStorageAndVerification() {
        // Test storing UPI PIN
        val storeResult = securityService.storeUpiPin(testUserId, testUpiPin)
        assertTrue("UPI PIN should be stored successfully", storeResult)

        // Test verifying correct PIN
        val verifyResult = securityService.verifyUpiPin(testUserId, testUpiPin)
        assertTrue("Correct PIN should be verified", verifyResult)

        // Test verifying incorrect PIN
        val verifyWrongResult = securityService.verifyUpiPin(testUserId, "9999")
        assertFalse("Incorrect PIN should not be verified", verifyWrongResult)
    }

    @Test
    fun testUpiPinChange() {
        // Setup initial PIN
        securityService.storeUpiPin(testUserId, testUpiPin)

        // Test changing PIN
        val changeResult = securityService.changeUpiPin(testUserId, testUpiPin, newUpiPin)
        assertTrue("PIN change should succeed", changeResult)

        // Verify old PIN no longer works
        val oldPinResult = securityService.verifyUpiPin(testUserId, testUpiPin)
        assertFalse("Old PIN should not work after change", oldPinResult)

        // Verify new PIN works
        val newPinResult = securityService.verifyUpiPin(testUserId, newUpiPin)
        assertTrue("New PIN should work after change", newPinResult)
    }

    @Test
    fun testUpiPinChange_InvalidOldPin() {
        // Setup initial PIN
        securityService.storeUpiPin(testUserId, testUpiPin)

        // Try changing with wrong old PIN
        val changeResult = securityService.changeUpiPin(testUserId, "9999", newUpiPin)
        assertFalse("PIN change should fail with wrong old PIN", changeResult)

        // Verify original PIN still works
        val verifyResult = securityService.verifyUpiPin(testUserId, testUpiPin)
        assertTrue("Original PIN should still work", verifyResult)
    }

    @Test
    fun testBiometricSettings() {
        // Test enabling biometric
        securityService.setBiometricEnabled(testUserId, true)
        var biometricEnabled = securityService.isBiometricEnabled(testUserId)
        assertTrue("Biometric should be enabled", biometricEnabled)

        // Test disabling biometric
        securityService.setBiometricEnabled(testUserId, false)
        biometricEnabled = securityService.isBiometricEnabled(testUserId)
        assertFalse("Biometric should be disabled", biometricEnabled)
    }

    @Test
    fun testAuthTokenManagement() {
        val testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature"

        // Test storing auth token
        securityService.storeAuthToken(testUserId, testToken)

        // Test retrieving auth token
        val retrievedToken = securityService.getAuthToken(testUserId)
        assertEquals("Retrieved token should match stored token", testToken, retrievedToken)
    }

    @Test
    fun testDataClearing() {
        // Setup data
        securityService.storeUpiPin(testUserId, testUpiPin)
        securityService.setBiometricEnabled(testUserId, true)
        securityService.storeAuthToken(testUserId, "test_token")

        // Verify data exists
        assertTrue("PIN should exist before clearing", securityService.verifyUpiPin(testUserId, testUpiPin))
        assertTrue("Biometric should be enabled before clearing", securityService.isBiometricEnabled(testUserId))
        assertNotNull("Auth token should exist before clearing", securityService.getAuthToken(testUserId))

        // Clear user data
        securityService.clearUserData(testUserId)

        // Verify data is cleared
        assertFalse("PIN should not exist after clearing", securityService.verifyUpiPin(testUserId, testUpiPin))
        assertFalse("Biometric should be disabled after clearing", securityService.isBiometricEnabled(testUserId))
        assertNull("Auth token should not exist after clearing", securityService.getAuthToken(testUserId))
    }

    @Test
    fun testTransactionKeyGeneration() {
        val key1 = securityService.generateTransactionKey()
        val key2 = securityService.generateTransactionKey()

        // Keys should be generated
        assertNotNull("Transaction key should not be null", key1)
        assertNotNull("Transaction key should not be null", key2)

        // Keys should be different (unique)
        assertNotEquals("Generated keys should be unique", key1, key2)

        // Keys should be non-empty
        assertTrue("Key should not be empty", key1.isNotEmpty())
        assertTrue("Key should not be empty", key2.isNotEmpty())
    }

    @Test
    fun testDataHashing() {
        val testData = "sensitive_payment_data"
        val hash1 = securityService.hashData(testData)
        val hash2 = securityService.hashData(testData)

        // Hashes should be consistent
        assertEquals("Same data should produce same hash", hash1, hash2)

        // Hash should be different for different data
        val differentHash = securityService.hashData("different_data")
        assertNotEquals("Different data should produce different hash", hash1, differentHash)

        // Hash should not be empty
        assertTrue("Hash should not be empty", hash1.isNotEmpty())
    }

    @Test
    fun testPinStrengthValidation() {
        // Test strong PINs
        assertTrue("PIN with digits should be strong", securityService.isPinStrong("1234"))
        assertTrue("PIN with digits and letters should be strong", securityService.isPinStrong("12a4"))

        // Test weak PINs
        assertFalse("PIN too short should be weak", securityService.isPinStrong("123"))
        assertFalse("PIN too long should be weak", securityService.isPinStrong("1234567"))
        assertFalse("PIN with only letters should be weak", securityService.isPinStrong("abcd"))
        assertFalse("Empty PIN should be weak", securityService.isPinStrong(""))
    }
}
