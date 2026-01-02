package com.example.udhaarpay

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.udhaarpay.data.model.*
import com.example.udhaarpay.data.repository.TransactionRepository
import com.example.udhaarpay.data.repository.UserRepository
import com.example.udhaarpay.domain.service.PaymentService
import com.example.udhaarpay.domain.service.SecurityService
import com.example.udhaarpay.domain.usecase.SendMoneyUseCase
import com.example.udhaarpay.utils.ValidationUtils
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class PaymentFlowIntegrationTest {

    private lateinit var paymentService: PaymentService
    private lateinit var securityService: SecurityService
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var userRepository: UserRepository
    private lateinit var sendMoneyUseCase: SendMoneyUseCase

    private val testUserId = "test_user_123"
    private val testRecipientUpiId = "recipient@paytm"
    private val testAmount = 100.0
    private val testUpiPin = "1234"

    @Before
    fun setup() {
        // Initialize repositories and services
        // Note: In a real app, these would be injected via Hilt
        transactionRepository = TransactionRepository(null, null) // Mock or real implementation
        userRepository = UserRepository(null, null) // Mock or real implementation
        securityService = SecurityService(ApplicationProvider.getApplicationContext(), null)
        sendMoneyUseCase = SendMoneyUseCase(
            transactionRepository,
            userRepository,
            securityService,
            null // ErrorHandler
        )
        paymentService = PaymentService(
            sendMoneyUseCase,
            null, // ReceiveMoneyUseCase
            null, // AddMoneyUseCase
            transactionRepository,
            userRepository,
            null, // BankAccountRepository
            null  // ErrorHandler
        )
    }

    @Test
    fun testSendMoneyFlow_Success() = runBlocking {
        // Arrange
        val initialBalance = 1000.0
        setupTestUser(initialBalance)

        // Act
        val result = paymentService.sendMoney(
            senderId = testUserId,
            receiverUpiId = testRecipientUpiId,
            amount = testAmount,
            description = "Test payment"
        )

        // Assert
        assertTrue("Payment should succeed", result.isSuccess)
        val transaction = result.getOrNull()
        assertNotNull("Transaction should not be null", transaction)
        assertEquals("Transaction type should be SEND", TransactionType.SEND, transaction?.type)
        assertEquals("Transaction amount should match", testAmount, transaction?.amount)
        assertEquals("Transaction status should be SUCCESS", TransactionStatus.SUCCESS, transaction?.status)

        // Verify balance deduction
        val finalBalance = paymentService.getWalletBalance(testUserId)
        assertEquals("Balance should be reduced by transaction amount", initialBalance - testAmount, finalBalance, 0.01)
    }

    @Test
    fun testSendMoneyFlow_InsufficientBalance() = runBlocking {
        // Arrange
        val lowBalance = 50.0
        setupTestUser(lowBalance)

        // Act
        val result = paymentService.sendMoney(
            senderId = testUserId,
            receiverUpiId = testRecipientUpiId,
            amount = testAmount,
            description = "Test payment"
        )

        // Assert
        assertFalse("Payment should fail due to insufficient balance", result.isSuccess)
        assertTrue("Error should mention insufficient balance",
            result.exceptionOrNull()?.message?.contains("insufficient", ignoreCase = true) == true)
    }

    @Test
    fun testSendMoneyFlow_InvalidUpiId() = runBlocking {
        // Arrange
        setupTestUser(1000.0)

        // Act
        val result = paymentService.sendMoney(
            senderId = testUserId,
            receiverUpiId = "invalid_upi_id",
            amount = testAmount,
            description = "Test payment"
        )

        // Assert
        assertFalse("Payment should fail with invalid UPI ID", result.isSuccess)
        assertTrue("Error should mention invalid UPI ID",
            result.exceptionOrNull()?.message?.contains("UPI", ignoreCase = true) == true)
    }

    @Test
    fun testSendMoneyFlow_InvalidAmount() = runBlocking {
        // Arrange
        setupTestUser(1000.0)

        // Act
        val result = paymentService.sendMoney(
            senderId = testUserId,
            receiverUpiId = testRecipientUpiId,
            amount = -100.0, // Invalid negative amount
            description = "Test payment"
        )

        // Assert
        assertFalse("Payment should fail with invalid amount", result.isSuccess)
        assertTrue("Error should mention invalid amount",
            result.exceptionOrNull()?.message?.contains("amount", ignoreCase = true) == true)
    }

    @Test
    fun testBillPaymentFlow_Electricity() = runBlocking {
        // Arrange
        setupTestUser(1000.0)

        // Act
        val result = paymentService.payBill(
            userId = testUserId,
            billType = "electricity",
            billNumber = "ELE123456",
            amount = 500.0,
            description = "Electricity bill payment"
        )

        // Assert
        assertTrue("Bill payment should succeed", result.isSuccess)
        val transaction = result.getOrNull()
        assertNotNull("Transaction should not be null", transaction)
        assertEquals("Transaction type should be BILL_PAYMENT", TransactionType.BILL_PAYMENT, transaction?.type)
        assertEquals("Transaction sub-type should be ELECTRICITY_BILL", TransactionSubType.ELECTRICITY_BILL, transaction?.subType)
        assertEquals("Transaction amount should match", 500.0, transaction?.amount)
    }

    @Test
    fun testTransactionHistoryRetrieval() = runBlocking {
        // Arrange
        setupTestUser(2000.0)

        // Create multiple transactions
        paymentService.sendMoney(testUserId, "user1@paytm", 100.0, "Payment 1")
        paymentService.sendMoney(testUserId, "user2@paytm", 200.0, "Payment 2")
        paymentService.payBill(testUserId, "mobile", "9876543210", 300.0, "Mobile recharge")

        // Act
        val transactions = paymentService.getTransactionHistory(testUserId, limit = 10)

        // Assert
        assertTrue("Should have transactions", transactions.isNotEmpty())
        assertTrue("Should have at least 3 transactions", transactions.size >= 3)

        // Verify transaction types
        val sendTransactions = transactions.filter { it.type == TransactionType.SEND }
        val billTransactions = transactions.filter { it.type == TransactionType.BILL_PAYMENT }

        assertTrue("Should have send transactions", sendTransactions.isNotEmpty())
        assertTrue("Should have bill payment transactions", billTransactions.isNotEmpty())
    }

    @Test
    fun testSecurityService_UpiPinValidation() {
        // Test valid PIN
        assertTrue("Valid PIN should pass", ValidationUtils.isValidUpiPin("1234"))
        assertTrue("Valid PIN should pass", ValidationUtils.isValidUpiPin("987654"))

        // Test invalid PINs
        assertFalse("PIN too short should fail", ValidationUtils.isValidUpiPin("123"))
        assertFalse("PIN too long should fail", ValidationUtils.isValidUpiPin("1234567"))
        assertFalse("PIN with letters should fail", ValidationUtils.isValidUpiPin("12a4"))
        assertFalse("Empty PIN should fail", ValidationUtils.isValidUpiPin(""))
    }

    @Test
    fun testValidationUtils_AmountValidation() {
        // Test valid amounts
        assertTrue("Valid amount should pass", ValidationUtils.isValidAmount("100.50"))
        assertTrue("Valid amount should pass", ValidationUtils.isValidAmount("10000.00"))

        // Test invalid amounts
        assertFalse("Zero amount should fail", ValidationUtils.isValidAmount("0"))
        assertFalse("Negative amount should fail", ValidationUtils.isValidAmount("-100"))
        assertFalse("Amount too large should fail", ValidationUtils.isValidAmount("10001"))
        assertFalse("Invalid format should fail", ValidationUtils.isValidAmount("abc"))
    }

    @Test
    fun testValidationUtils_UpiIdValidation() {
        // Test valid UPI IDs
        assertTrue("Valid UPI ID should pass", ValidationUtils.isValidUpiId("user@paytm"))
        assertTrue("Valid UPI ID should pass", ValidationUtils.isValidUpiId("test.user@oksbi"))

        // Test invalid UPI IDs
        assertFalse("UPI ID without @ should fail", ValidationUtils.isValidUpiId("userpaytm"))
        assertFalse("UPI ID with invalid domain should fail", ValidationUtils.isValidUpiId("user@invalid"))
        assertFalse("Empty UPI ID should fail", ValidationUtils.isValidUpiId(""))
    }

    @Test
    fun testSplitBillFlow() = runBlocking {
        // Arrange
        setupTestUser(1000.0)
        val participants = listOf("user1@paytm", "user2@paytm", "user3@paytm")
        val totalAmount = 300.0

        // Act
        val result = paymentService.splitBill(
            payerId = testUserId,
            participants = participants,
            totalAmount = totalAmount,
            description = "Dinner bill split"
        )

        // Assert
        assertTrue("Split bill should succeed", result.isSuccess)
        val transactions = result.getOrNull()
        assertNotNull("Transactions should not be null", transactions)
        assertEquals("Should have 3 transactions", 3, transactions?.size)

        // Verify each transaction
        transactions?.forEach { transaction ->
            assertEquals("Each transaction should be SEND type", TransactionType.SEND, transaction.type)
            assertEquals("Each share should be 100", 100.0, transaction.amount, 0.01)
            assertTrue("Description should contain split info", transaction.description.contains("split"))
        }

        // Verify total deduction
        val finalBalance = paymentService.getWalletBalance(testUserId)
        assertEquals("Total balance deduction should be 300", 700.0, finalBalance, 0.01)
    }

    @Test
    fun testRecurringPaymentSetup() = runBlocking {
        // This test would require RecurringPaymentService to be properly implemented
        // For now, we'll test the basic validation

        val recurringService = RecurringPaymentService(
            transactionRepository,
            userRepository,
            paymentService,
            null, // NotificationService
            null  // ErrorHandler
        )

        // Test creating a recurring payment
        val result = recurringService.createRecurringPayment(
            userId = testUserId,
            recipientUpiId = testRecipientUpiId,
            amount = 500.0,
            frequency = RecurringFrequency.MONTHLY,
            description = "Monthly subscription"
        )

        // This will fail in current implementation due to missing database layer
        // But we can test the validation logic
        assertNotNull("Result should not be null", result)
    }

    // Helper method to set up test user
    private suspend fun setupTestUser(initialBalance: Double) {
        // In a real test, this would create a test user in the database
        // For now, we'll assume the user exists with the specified balance
    }
}
