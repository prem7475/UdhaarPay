package com.udhaarpay.app.data.local.dao

import androidx.room.*
import com.udhaarpay.app.data.local.entities.Debt
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: Debt): Long

    @Update
    suspend fun update(debt: Debt): Int

    @Delete
    suspend fun delete(debt: Debt): Int

    @Query("SELECT * FROM debts")
    fun getAll(): Flow<List<Debt>>

    @Query("SELECT * FROM debts WHERE personName = :personName")
    fun getByPersonName(personName: String): Flow<List<Debt>>

    @Query("SELECT * FROM debts WHERE type = :type")
    fun getByType(type: String): Flow<List<Debt>>

    @Query("SELECT * FROM debts WHERE status = :status")
    fun getByStatus(status: String): Flow<List<Debt>>
}
