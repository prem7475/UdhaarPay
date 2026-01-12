package com.udhaarpay.app.data.local.dao

import androidx.room.*
import com.udhaarpay.app.data.local.entities.Investment
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(investment: Investment): Long

    @Update
    suspend fun update(investment: Investment): Int

    @Delete
    suspend fun delete(investment: Investment): Int

    @Query("SELECT * FROM investments")
    fun getAll(): Flow<List<Investment>>

    @Query("SELECT * FROM investments WHERE brokerName = :brokerName")
    fun getByBroker(brokerName: String): Flow<List<Investment>>

    @Query("SELECT * FROM investments WHERE type = :type")
    fun getByType(type: String): Flow<List<Investment>>

    @Query("SELECT SUM(currentValue) FROM investments")
    fun getSummary(): Flow<Double?>
}
