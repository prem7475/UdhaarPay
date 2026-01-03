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

class SendMoneyUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(
        senderId: String,
        receiverUpiId: String,
        amount: Double,
        description: String,
        paymentMethod: PaymentMethod = PaymentMethod.WALLET
    ): Result<Transaction> {
        return try {
            // Validate inputs
            if (!ValidationUtils.isValidAmount(amount.toString())) {
                return Result.failure(Exception("Invalid amount"))
            }
            if (!ValidationUtils.isValidUpiId(receiverUpiId)) {
                return Result.failure(Exception("Invalid UPI ID"))
            }

            // Get sender's wallet balance
            val senderUser = userRepository.getUserById(senderId).first()
            if (senderUser?.walletBalance ?: 0.0 < amount) {
                return Result.failure(Exception("Insufficient balance"))
            }

            // Create transaction

            val transaction = Transaction(
                userId = senderId,
                transactionId = UUID.randomUUID().toString(),
                type = TransactionType.SEND_MONEY,
                description = description,
                amount = amount,
                fee = 0.0,
                totalAmount = amount,
                status = TransactionStatus.PENDING,
                senderUpiId = senderUser?.upiId ?: "",
                receiverId = senderId,
                receiverName = senderUser?.name ?: "",
                receiverUpiId = receiverUpiId,
                paymentMethod = paymentMethod,
                category = "Send Money",
                isDebit = true
            )

            // Save transaction
            transactionRepository.insertTransaction(transaction)

            // Update sender's wallet
            userRepository.updateUserWalletBalance(senderId, (senderUser?.walletBalance ?: 0.0) - amount)

            Result.success(transaction)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
