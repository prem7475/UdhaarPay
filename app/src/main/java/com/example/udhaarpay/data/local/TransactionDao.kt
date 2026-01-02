package com.example.udhaarpay.data.local

import androidx.room.*
import com.example.udhaarpay.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE isDebit = 1 ORDER BY timestamp DESC")
    fun getDebitTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE isDebit = 0 ORDER BY timestamp DESC")
    fun getCreditTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY timestamp DESC")
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 10): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE isDebit = 0")
    fun getTotalCredits(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE isDebit = 1")
    fun getTotalDebits(): Flow<Double?>

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("SELECT COUNT(*) FROM transactions")
    fun getTransactionCount(): Flow<Int>
}
