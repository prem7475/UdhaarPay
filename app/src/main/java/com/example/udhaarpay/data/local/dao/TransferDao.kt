package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.udhaarpay.data.model.Transfer
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferDao {
    @Insert
    suspend fun insertTransfer(transfer: Transfer): Long

    @Query("SELECT * FROM transfers WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserTransfers(userId: Long): Flow<List<Transfer>>

    @Query("SELECT * FROM transfers WHERE id = :transferId")
    suspend fun getTransferById(transferId: Long): Transfer?

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transfers WHERE userId = :userId AND fromAccount = :from AND toAccount = :to")
    suspend fun getTotalTransferred(userId: Long, from: String, to: String): Double
}
