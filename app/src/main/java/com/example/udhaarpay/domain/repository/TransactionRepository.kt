package com.example.udhaarpay.domain.repository

import com.example.udhaarpay.data.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    suspend fun insertTransaction(transaction: Transaction): Long

    suspend fun insertTransactions(transactions: List<Transaction>)

    fun getAllTransactions(): Flow<List<Transaction>>

    fun getDebitTransactions(): Flow<List<Transaction>>

    fun getCreditTransactions(): Flow<List<Transaction>>

    fun getTransactionsByCategory(category: String): Flow<List<Transaction>>

    fun getRecentTransactions(limit: Int = 10): Flow<List<Transaction>>

    fun getTotalCredits(): Flow<Double?>

    fun getTotalDebits(): Flow<Double?>

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun deleteAllTransactions()

    fun getTransactionCount(): Flow<Int>
}
