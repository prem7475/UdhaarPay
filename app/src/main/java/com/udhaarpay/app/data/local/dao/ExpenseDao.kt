package com.udhaarpay.app.data.local.dao

import androidx.room.*
import com.udhaarpay.app.data.local.entities.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long

    @Delete
    suspend fun delete(expense: Expense): Int

    @Query("SELECT * FROM expenses")
    fun getAll(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category")
    fun getByCategory(category: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date = :date")
    fun getByDate(date: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE month = :month")
    fun getByMonth(month: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE account = :account")
    fun getByAccount(account: String): Flow<List<Expense>>

    @Update
    suspend fun update(expense: Expense): Int
}
