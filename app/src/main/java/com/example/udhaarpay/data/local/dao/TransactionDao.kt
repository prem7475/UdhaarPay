package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.udhaarpay.data.model.Transaction 
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // IMPORTANT: Changed ORDER BY date -> ORDER BY timestamp
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllTransactions(userId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'DEBIT'")
    fun getTotalSpent(userId: String): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'CREDIT'")
    fun getTotalReceived(userId: String): Flow<Double?>
}