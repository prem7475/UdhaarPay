package com.udhaarpay.app.repository

import com.udhaarpay.app.data.local.dao.UPIPaymentDao
import com.udhaarpay.app.data.local.entities.UPIPayment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UPIPaymentRepository @Inject constructor(
    private val upiPaymentDao: UPIPaymentDao
) {
    fun getAll(): Flow<List<UPIPayment>> = upiPaymentDao.getAll()
    suspend fun insert(payment: UPIPayment): Long = upiPaymentDao.insert(payment)
    suspend fun delete(payment: UPIPayment): Int = upiPaymentDao.delete(payment)
}
