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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: BankAccount): Long

    @Update
    suspend fun update(account: BankAccount): Int

    @Delete
    suspend fun delete(account: BankAccount): Int

    @Query("SELECT * FROM bank_accounts")
    fun getAll(): Flow<List<BankAccount>>

    @Query("SELECT * FROM bank_accounts WHERE accountId = :accountId LIMIT 1")
    fun getById(accountId: Long): Flow<BankAccount?>

    @Query("SELECT * FROM bank_accounts WHERE bankName = :bankName")
    fun getByBankName(bankName: String): Flow<List<BankAccount>>
}
