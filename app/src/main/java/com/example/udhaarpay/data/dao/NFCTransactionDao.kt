package com.example.udhaarpay.data.dao

import androidx.room.*
import com.example.udhaarpay.data.model.NFCTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface NFCTransactionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: NFCTransaction)
    
    @Query("SELECT * FROM nfc_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserNFCTransactions(userId: Int): Flow<List<NFCTransaction>>
    
    @Query("SELECT COUNT(*) FROM nfc_transactions WHERE userId = :userId")
    fun getNFCTransactionCount(userId: Int): Flow<Int>
    
    @Query("SELECT SUM(amount) FROM nfc_transactions WHERE userId = :userId")
    fun getTotalNFCSpent(userId: Int): Flow<Double?>
    
    @Query("DELETE FROM nfc_transactions WHERE userId = :userId")
    suspend fun deleteAllUserTransactions(userId: Int)
}
