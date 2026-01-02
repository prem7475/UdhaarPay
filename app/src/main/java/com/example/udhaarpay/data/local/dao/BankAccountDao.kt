package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.udhaarpay.data.local.entity.BankAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {
    @Query("SELECT * FROM bank_accounts")
    fun getAllBankAccounts(): Flow<List<BankAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccount(account: BankAccountEntity)

    @Query("SELECT * FROM bank_accounts WHERE id = :id")
    suspend fun getBankAccountById(id: Long): BankAccountEntity?

    @Delete
    suspend fun deleteBankAccount(account: BankAccountEntity)
}
