package com.example.udhaarpay.domain.usecase

import com.example.udhaarpay.data.model.Transaction
import com.example.udhaarpay.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> {
        return transactionRepository.getAllTransactions()
    }
}
