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

class AddMoneyUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(
        userId: String,
        amount: Double,
        paymentMethod: PaymentMethod,
        description: String = "Add money to wallet"
    ): Result<Transaction> {
        return try {
            // Validate inputs
            if (!ValidationUtils.isValidAmount(amount.toString())) {
                return Result.failure(Exception("Invalid amount"))
            }

            // Get user
            val user = userRepository.getUserById(userId).first()

            // Create transaction

            val transaction = Transaction(
                userId = userId,
                transactionId = UUID.randomUUID().toString(),
                type = TransactionType.ADD_MONEY,
                description = description,
                amount = amount,
                fee = 0.0,
                totalAmount = amount,
                status = TransactionStatus.COMPLETED,
                senderUpiId = user?.upiId ?: "",
                receiverId = userId,
                receiverName = user?.name ?: "",
                receiverUpiId = user?.upiId ?: "",
                paymentMethod = paymentMethod,
                category = "Add Money",
                isDebit = false
            )

            // Save transaction
            transactionRepository.insertTransaction(transaction)

            // Update user's wallet
            userRepository.updateUserWalletBalance(userId, (user?.walletBalance ?: 0.0) + amount)

            Result.success(transaction)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
