package com.udhaarpay.app.repository

import com.udhaarpay.app.data.local.dao.BankAccountDao
import com.udhaarpay.app.data.local.entities.BankAccount
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BankAccountRepository @Inject constructor(
    private val bankAccountDao: BankAccountDao
) {
    fun getAll(): Flow<List<BankAccount>> = bankAccountDao.getAll()
    suspend fun insert(account: BankAccount): Long = bankAccountDao.insert(account)
    suspend fun delete(account: BankAccount): Int = bankAccountDao.delete(account)
    suspend fun update(account: BankAccount): Int = bankAccountDao.update(account)
}
