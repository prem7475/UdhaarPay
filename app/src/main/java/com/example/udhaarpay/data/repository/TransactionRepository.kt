package com.example.udhaarpay.data.repository

import com.example.udhaarpay.data.local.TransactionDao
import com.example.udhaarpay.data.model.Transaction
import com.example.udhaarpay.data.remote.ApiService
import com.example.udhaarpay.data.remote.FirebaseRealtimeService
import com.example.udhaarpay.data.remote.FirebaseMonitoringService
import com.example.udhaarpay.utils.ErrorHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Date
import com.example.udhaarpay.data.model.TransactionStatus
import kotlinx.coroutines.flow.map

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val apiService: ApiService,
    private val firebaseDatabaseService: FirebaseRealtimeService,
    private val firebaseMonitoringService: FirebaseMonitoringService,
    private val errorHandler: ErrorHandler
) {

    // Get all transactions from local database with Firebase sync
    fun getAllTransactions(): Flow<List<Transaction>> {
        // Just return local transactions for now; Firebase sync can be handled elsewhere
        return transactionDao.getAllTransactions()
    }

    // Get transactions by type
    fun getTransactionsByType(type: String): Flow<List<Transaction>> {
        return if (type == "debit") {
            transactionDao.getDebitTransactions()
        } else {
            transactionDao.getCreditTransactions()
        }
    }

    // Insert transaction locally and sync to Firebase
    suspend fun insertTransaction(transaction: Transaction) {
        try {
            // Insert locally first for immediate UI update
            transactionDao.insertTransaction(transaction)
            // TODO: Implement Firebase sync if needed
        } catch (e: Exception) {
            errorHandler.handleError(e, "TransactionRepository.insertTransaction")
            throw e
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        // Stub: Implement update logic here
        transactionDao.updateTransaction(transaction)
    }

    suspend fun processUPIPayment(
        fromAccount: String,
        toUPI: String,
        amount: Double,
        upiPin: String,
        category: String,
        remarks: String
    ): Result<Map<String, String>> {
        return try {
            // Stub: Implement UPI payment processing logic
            Result.success(mapOf("transactionId" to "TXN_${System.currentTimeMillis()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Insert multiple transactions
    suspend fun insertTransactions(transactions: List<Transaction>) {
        try {
            // Insert locally first
            transactionDao.insertTransactions(transactions)
            // TODO: Implement Firebase batch sync if needed
        } catch (e: Exception) {
            errorHandler.handleError(e, "TransactionRepository.insertTransactions")
            throw e
        }
    }

    // Delete transaction locally and from Firebase
    suspend fun deleteTransaction(transaction: Transaction) {
        try {
            // Delete locally first
            transactionDao.deleteTransaction(transaction)
            // TODO: Implement Firebase delete if needed
        } catch (e: Exception) {
            errorHandler.handleError(e, "TransactionRepository.deleteTransaction")
            throw e
        }
    }

    // Sync transactions from Firebase
    private suspend fun syncTransactionsFromFirebase() {
        // TODO: Implement Firebase sync logic using observeUserTransactions
    }

    // Get current user ID (placeholder - implement based on your auth system)
    private suspend fun getCurrentUserId(): String {
        // TODO: Implement based on your authentication system
        return "" // Return actual user ID
    }

    // Create transaction with Firebase sync
    suspend fun createTransaction(transaction: Transaction): Result<Transaction> {
        return try {
            // Create transaction with Firebase sync
            insertTransaction(transaction)
            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get recent transactions (last 10)
    fun getRecentTransactions(): Flow<List<Transaction>> =
        transactionDao.getRecentTransactions()

    // Get transactions in a date range with optional filters. Accepts userId as String (phone or id) for compatibility.
    suspend fun getTransactionsInDateRange(
        userId: String,
        startDate: Date,
        endDate: Date,
        isDebit: Boolean? = null,
        status: TransactionStatus? = null
    ): List<Transaction> {
        // Convert userId to numeric DB id when possible; otherwise try 0 (no results expected)
        val all: List<Transaction> = try {
            // Use getAllTransactions and filter by userId
            transactionDao.getAllTransactions().first().filter { it.userId == userId }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching transactions for userId=$userId")
            emptyList()
        }

        return all.filter { tx: Transaction ->
            val ts = tx.timestamp
            val inRange = ts >= startDate && ts <= endDate
            val debitMatch = isDebit == null || tx.isDebit == isDebit
            val statusMatch = status == null || tx.status == status
            inRange && debitMatch && statusMatch
        }
    }

    // Get transaction count
    suspend fun getTransactionCount(): Flow<Int> = transactionDao.getTransactionCount()

    // Clear all transactions
    suspend fun clearAllTransactions() {
        transactionDao.deleteAllTransactions()
    }
}
