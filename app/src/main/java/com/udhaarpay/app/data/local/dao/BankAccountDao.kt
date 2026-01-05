package com.udhaarpay.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.udhaarpay.app.data.local.entities.BankAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {
    @Query("SELECT * FROM bank_accounts WHERE userId = :userId")
    fun getAccountsForUser(userId: String): Flow<List<BankAccount>>

    @Query("SELECT * FROM bank_accounts")
    fun getAllBankAccounts(): Flow<List<BankAccount>>

    @Query("SELECT * FROM bank_accounts WHERE id = :accountId")
    fun getAccountById(accountId: Long): BankAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccount(account: BankAccount): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBankAccount(account: BankAccount): Long

    @Update
    fun updateAccount(account: BankAccount)

    @Delete
    fun deleteAccount(account: BankAccount)

    @Delete
    fun deleteBankAccount(account: BankAccount)
}
