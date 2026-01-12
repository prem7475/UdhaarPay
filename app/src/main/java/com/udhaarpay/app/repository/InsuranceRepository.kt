package com.udhaarpay.app.repository

import com.udhaarpay.app.data.local.dao.InsuranceDao
import com.udhaarpay.app.data.local.entities.Insurance
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsuranceRepository @Inject constructor(
    private val insuranceDao: InsuranceDao
) {
    fun getAll(): Flow<List<Insurance>> = insuranceDao.getAll()
    suspend fun insert(insurance: Insurance): Long = insuranceDao.insert(insurance)
    suspend fun delete(insurance: Insurance): Int = insuranceDao.delete(insurance)
    suspend fun update(insurance: Insurance): Int = insuranceDao.update(insurance)
}
