package com.example.udhaarpay.data.p2p

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.udhaarpay.data.model.Transaction
import com.example.udhaarpay.data.model.TransactionType
import com.example.udhaarpay.data.model.User
import com.example.udhaarpay.data.repository.TransactionRepository
import com.example.udhaarpay.data.security.EncryptionManager
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class P2PTransferManager @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val encryptionManager: EncryptionManager
) {

    private val _transferResult = MutableLiveData<TransferResult>()
    val transferResult: LiveData<TransferResult> = _transferResult

    private val _transferHistory = MutableLiveData<List<P2PTransfer>>()
    val transferHistory: LiveData<List<P2PTransfer>> = _transferHistory

    companion object {
        private const val TAG = "P2PTransferManager"
        private const val MAX_P2P_AMOUNT = 50000.0 // ₹50,000 max for P2P
        private const val MIN_P2P_AMOUNT = 1.0 // ₹1 minimum
    }

    /**
     * Send money to another user
     */
    suspend fun sendMoney(
        recipientId: String,
        recipientName: String,
        amount: Double,
        description: String = "P2P Transfer"
    ): Boolean {
        try {
            // Validate amount
            if (!validateAmount(amount)) {
                _transferResult.value = TransferResult.Failure("Invalid amount")
                return false
            }

            // Check sender balance
            val currentBalance = encryptionManager.getWalletBalance()
            if (currentBalance < amount) {
                _transferResult.value = TransferResult.Failure("Insufficient wallet balance")
                return false
            }

            // In a real implementation, you would:
            // 1. Verify recipient exists in the system
            // 2. Check transfer limits and compliance
            // 3. Process the transfer via backend API
            // 4. Update both sender and receiver balances

            // For demo purposes, simulate the transfer
            simulateTransfer(recipientId, recipientName, amount, description)

            Timber.d(TAG, "P2P transfer initiated: $amount to $recipientName ($recipientId)")
            return true

        } catch (e: Exception) {
            Timber.e(TAG, "P2P transfer failed", e)
            _transferResult.value = TransferResult.Failure("Transfer failed: ${e.message}")
            return false
        }
    }

    /**
     * Request money from another user
     */
    suspend fun requestMoney(
        requesterId: String,
        requesterName: String,
        amount: Double,
        description: String = "Payment Request"
    ): Boolean {
        try {
            if (!validateAmount(amount)) {
                _transferResult.value = TransferResult.Failure("Invalid amount")
                return false
            }

            // In a real implementation, this would send a notification/request to the recipient
            // For demo, we'll just log it
            Timber.d(TAG, "Money request sent: $amount from $requesterName ($requesterId)")

            _transferResult.value = TransferResult.RequestSent(
                requesterId = requesterId,
                requesterName = requesterName,
                amount = amount,
                description = description
            )

            return true

        } catch (e: Exception) {
            Timber.e(TAG, "Money request failed", e)
            _transferResult.value = TransferResult.Failure("Request failed: ${e.message}")
            return false
        }
    }

    /**
     * Accept a money request
     */
    suspend fun acceptMoneyRequest(
        _requestId: String,
        requesterId: String,
        requesterName: String,
        amount: Double,
        description: String
    ): Boolean {
        return sendMoney(requesterId, requesterName, amount, "Payment for: $description")
    }

    /**
     * Get P2P transfer history
     */
    suspend fun getTransferHistory(): List<P2PTransfer> {
        try {
            // In a real implementation, this would fetch from backend
            // For demo, we'll create mock data
            val mockTransfers = createMockTransferHistory()
            _transferHistory.value = mockTransfers
            return mockTransfers

        } catch (e: Exception) {
            Timber.e(TAG, "Failed to get transfer history", e)
            return emptyList()
        }
    }

    /**
     * Validate transfer amount
     */
    private fun validateAmount(amount: Double): Boolean {
        return amount >= MIN_P2P_AMOUNT && amount <= MAX_P2P_AMOUNT
    }

    /**
     * Simulate P2P transfer (in real app, this would be API calls)
     */
    private suspend fun simulateTransfer(
        recipientId: String,
        recipientName: String,
        amount: Double,
        description: String
    ) {
        try {
            // Deduct from sender's wallet
            val currentBalance = encryptionManager.getWalletBalance()
            val newBalance = currentBalance - amount
            encryptionManager.saveWalletBalance(newBalance)

            // Create transaction record
            val transaction = Transaction(
                id = System.currentTimeMillis(),
                userId = "", // TODO: Provide actual userId
                transactionId = UUID.randomUUID().toString(),
                type = TransactionType.P2P_SEND,
                subType = null,
                description = "Sent to $recipientName",
                amount = amount,
                fee = 0.0,
                totalAmount = amount,
                timestamp = Date(),
                status = com.example.udhaarpay.data.model.TransactionStatus.COMPLETED,
                senderId = "", // TODO: Provide actual senderId
                senderName = "", // TODO: Provide actual senderName
                senderUpiId = null,
                receiverId = recipientId,
                receiverName = recipientName,
                receiverUpiId = null,
                bankAccountId = null,
                paymentMethod = com.example.udhaarpay.data.model.PaymentMethod.WALLET,
                category = "P2P Transfer",
                merchantName = null,
                merchantCategory = null,
                location = null,
                notes = description,
                referenceNumber = null,
                isRecurring = false,
                recurringId = null,
                isDebit = true,
                balanceAfter = newBalance,
                metadata = null,
                createdAt = Date(),
                updatedAt = Date()
            )

            transactionRepository.insertTransaction(transaction)

            // Simulate network delay
            kotlinx.coroutines.delay(1500)

            // Return success
            val transfer = P2PTransfer(
                id = "P2P_${System.currentTimeMillis()}",
                type = P2PTransferType.SENT,
                recipientId = recipientId,
                recipientName = recipientName,
                amount = amount,
                description = description,
                timestamp = Date(),
                status = P2PTransferStatus.COMPLETED
            )

            _transferResult.value = TransferResult.Success(transfer)

        } catch (e: Exception) {
            Timber.e(TAG, "Transfer simulation failed", e)
            _transferResult.value = TransferResult.Failure("Transfer failed: ${e.message}")
        }
    }

    /**
     * Create mock transfer history for demo
     */
    private fun createMockTransferHistory(): List<P2PTransfer> {
        val calendar = Calendar.getInstance()
        return listOf(
            P2PTransfer(
                id = "P2P_001",
                type = P2PTransferType.SENT,
                recipientId = "user123",
                recipientName = "John Doe",
                amount = 500.0,
                description = "Lunch payment",
                timestamp = calendar.apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
                status = P2PTransferStatus.COMPLETED
            ),
            P2PTransfer(
                id = "P2P_002",
                type = P2PTransferType.RECEIVED,
                recipientId = "user456",
                recipientName = "Jane Smith",
                amount = 1000.0,
                description = "Movie tickets",
                timestamp = calendar.apply { add(Calendar.DAY_OF_MONTH, -3) }.time,
                status = P2PTransferStatus.COMPLETED
            ),
            P2PTransfer(
                id = "P2P_003",
                type = P2PTransferType.SENT,
                recipientId = "user789",
                recipientName = "Bob Wilson",
                amount = 250.0,
                description = "Coffee",
                timestamp = calendar.apply { add(Calendar.DAY_OF_MONTH, -5) }.time,
                status = P2PTransferStatus.COMPLETED
            )
        )
    }

    // P2P Transfer data classes
    data class P2PTransfer(
        val id: String,
        val type: P2PTransferType,
        val recipientId: String,
        val recipientName: String,
        val amount: Double,
        val description: String,
        val timestamp: Date,
        val status: P2PTransferStatus
    )

    enum class P2PTransferType {
        SENT, RECEIVED, REQUESTED
    }

    enum class P2PTransferStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }

    // Transfer Result sealed class
    sealed class TransferResult {
        data class Success(val transfer: P2PTransfer) : TransferResult()
        data class RequestSent(
            val requesterId: String,
            val requesterName: String,
            val amount: Double,
            val description: String
        ) : TransferResult()
        data class Failure(val reason: String) : TransferResult()
    }
}
