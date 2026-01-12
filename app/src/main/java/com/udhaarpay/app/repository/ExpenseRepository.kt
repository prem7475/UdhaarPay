package com.udhaarpay.app.repository

import com.udhaarpay.app.data.local.dao.ExpenseDao
import com.udhaarpay.app.data.local.entities.Expense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    fun getAll(): Flow<List<Expense>> = expenseDao.getAll()
    suspend fun insert(expense: Expense): Long = expenseDao.insert(expense)
    suspend fun delete(expense: Expense): Int = expenseDao.delete(expense)
    suspend fun update(expense: Expense): Int = expenseDao.update(expense)
}
