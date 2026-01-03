package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.udhaarpay.data.model.BankAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {
    @Query("SELECT * FROM bank_accounts WHERE userId = :userId")
    fun getAccountsForUser(userId: String): Flow<List<BankAccount>>

    @Query("SELECT * FROM bank_accounts")
    fun getAllBankAccounts(): Flow<List<BankAccount>>

    @Query("SELECT * FROM bank_accounts WHERE id = :accountId")
    suspend fun getAccountById(accountId: Long): BankAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: BankAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccount(account: BankAccount)

    @Update
    suspend fun updateAccount(account: BankAccount)

    @Delete
    suspend fun deleteAccount(account: BankAccount)

    @Delete
    suspend fun deleteBankAccount(account: BankAccount)
}