package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.udhaarpay.data.local.entity.UdhariRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UdhariDao {
    @Query("SELECT * FROM udhari_records ORDER BY date DESC")
    fun getAllUdhariRecords(): Flow<List<UdhariRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUdhariRecord(record: UdhariRecordEntity)

    @Query("SELECT * FROM udhari_records WHERE isSettled = 0")
    fun getActiveUdhariRecords(): Flow<List<UdhariRecordEntity>>
    
    @Query("UPDATE udhari_records SET isSettled = 1 WHERE id = :id")
    suspend fun settleUdhari(id: Long)
}
