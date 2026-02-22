package com.udhaarpay.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udhaarpay.app.data.local.entities.NFCTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NFCTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: NFCTransactionEntity): Long

    @Query("SELECT * FROM nfc_transactions ORDER BY timestamp DESC")
    fun getAll(): Flow<List<NFCTransactionEntity>>

    @Query("SELECT * FROM nfc_transactions WHERE cardId = :cardId ORDER BY timestamp DESC")
    fun getByCardId(cardId: Long): Flow<List<NFCTransactionEntity>>
}
