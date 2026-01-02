package com.example.udhaarpay.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.udhaarpay.data.model.BankAccount // Correct import added

@Dao
interface BankAccountDao {

    @Insert
    suspend fun addBankAccount(bankAccount: BankAccount)

    @Delete
    suspend fun deleteBankAccount(bankAccount: BankAccount)

    @Query("SELECT * FROM bank_accounts ORDER BY id ASC")
    fun readAllData(): LiveData<List<BankAccount>>
}
