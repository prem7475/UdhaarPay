package com.example.udhaarpay.domain.usecase

import com.example.udhaarpay.data.model.PaymentMethod
import com.example.udhaarpay.data.model.Transaction
import com.example.udhaarpay.data.model.TransactionStatus
import com.example.udhaarpay.data.model.TransactionType
import com.example.udhaarpay.data.repository.TransactionRepository
import com.example.udhaarpay.data.repository.UserRepository
import com.example.udhaarpay.utils.ErrorHandler
import com.example.udhaarpay.utils.ValidationUtils
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class ReceiveMoneyUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) {

    suspend operator fun invoke(
        receiverId: String,
        senderUpiId: String,
        amount: Double,
        description: String,
        paymentMethod: PaymentMethod = PaymentMethod.WALLET
    ): Result<Transaction> {
        return try {
            // Validate inputs
            if (!ValidationUtils.isValidAmount(amount.toString())) {
                return Result.failure(Exception("Invalid amount"))
            }

            if (!ValidationUtils.isValidUpiId(senderUpiId)) {
                return Result.failure(Exception("Invalid sender UPI ID"))
            }

            // Get receiver
            val receiver = userRepository.getUserById(receiverId).first()
                ?: return Result.failure(Exception("Receiver not found"))

            // Calculate fee (no fee for receiving money)
            val fee = 0.0
            val totalAmount = amount

            // Create transaction

            val transaction = Transaction(
                userId = receiverId,
                transactionId = generateTransactionId(),
                type = TransactionType.RECEIVE,
                description = description,
                amount = amount,
                fee = 0.0,
                totalAmount = amount,
                status = TransactionStatus.PENDING,
                senderUpiId = senderUpiId,
                receiverId = receiverId,
                receiverName = receiver.name,
                receiverUpiId = receiver.upiId,
                paymentMethod = paymentMethod,
                category = "Received",
                isDebit = false
            )

            // Save transaction
            transactionRepository.insertTransaction(transaction)

            // Update receiver balance
            val currentBalance = receiver.walletBalance
            val newBalance = currentBalance + amount
            userRepository.updateWalletBalance(receiverId, newBalance)

            // Update transaction status to success
            val successTransaction = transaction.copy(
                status = TransactionStatus.SUCCESS,
                updatedAt = Date()
            )
            transactionRepository.updateTransaction(successTransaction)

            Result.success(successTransaction)

        } catch (e: Exception) {
            errorHandler.handleError(e, "ReceiveMoneyUseCase")
            Result.failure(e)
        }
    }

    private fun generateTransactionId(): String {
        return "TXN${System.currentTimeMillis()}${UUID.randomUUID().toString().take(8).uppercase()}"
    }
}
