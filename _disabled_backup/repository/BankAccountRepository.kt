package com.example.udhaarpay.data.repository

import com.example.udhaarpay.data.database.BankAccountDao
import com.example.udhaarpay.data.model.BankAccount
import com.example.udhaarpay.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BankAccountRepository @Inject constructor(
    private val bankAccountDao: BankAccountDao,
    private val apiService: ApiService
) {

    // Get all bank accounts for user
    fun getBankAccountsForUser(userId: Int): Flow<List<BankAccount>> {
        return bankAccountDao.getBankAccountsFlow(userId)
    }

    // Add new bank account
    suspend fun addBankAccount(bankAccount: BankAccount) {
        bankAccountDao.insertBankAccount(bankAccount)
    }

    // Delete bank account
    suspend fun deleteBankAccount(bankAccount: BankAccount) {
        bankAccountDao.deleteBankAccount(bankAccount)
    }
}
