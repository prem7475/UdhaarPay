package com.udhaarpay.app.repository

import com.udhaarpay.app.data.local.dao.CreditCardDao
import com.udhaarpay.app.data.local.entities.CreditCard
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditCardRepository @Inject constructor(
    private val creditCardDao: CreditCardDao
) {
    fun getAll(): Flow<List<CreditCard>> = creditCardDao.getAll()
    suspend fun insert(card: CreditCard): Long = creditCardDao.insert(card)
    suspend fun delete(card: CreditCard): Int = creditCardDao.delete(card)
    suspend fun update(card: CreditCard): Int = creditCardDao.update(card)
}
