package com.udhaarpay.app.repository

import com.udhaarpay.app.data.local.dao.InvestmentDao
import com.udhaarpay.app.data.local.entities.Investment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvestmentRepository @Inject constructor(
    private val investmentDao: InvestmentDao
) {
    fun getAll(): Flow<List<Investment>> = investmentDao.getAll()
    fun getSummary(): Flow<Double?> = investmentDao.getSummary()
    suspend fun insert(investment: Investment): Long = investmentDao.insert(investment)
    suspend fun delete(investment: Investment): Int = investmentDao.delete(investment)
    suspend fun update(investment: Investment): Int = investmentDao.update(investment)
}
