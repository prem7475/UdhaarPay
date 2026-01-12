package com.udhaarpay.app.repository

import com.udhaarpay.app.data.local.dao.DebtDao
import com.udhaarpay.app.data.local.entities.Debt
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtRepository @Inject constructor(
    private val debtDao: DebtDao
) {
    fun getAll(): Flow<List<Debt>> = debtDao.getAll()
    suspend fun insert(debt: Debt): Long = debtDao.insert(debt)
    suspend fun delete(debt: Debt): Int = debtDao.delete(debt)
    suspend fun update(debt: Debt): Int = debtDao.update(debt)
}
