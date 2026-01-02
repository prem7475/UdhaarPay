package com.example.udhaarpay.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.udhaarpay.data.local.dao.BankAccountDao
import com.example.udhaarpay.data.local.dao.CreditCardDao
import com.example.udhaarpay.data.local.dao.ExpenseDao
import com.example.udhaarpay.data.local.dao.TransactionDao
import com.example.udhaarpay.data.local.dao.UdhariDao
import com.example.udhaarpay.data.local.dao.UserDao
import com.example.udhaarpay.data.local.entity.BankAccountEntity
import com.example.udhaarpay.data.local.entity.CreditCardEntity
import com.example.udhaarpay.data.local.entity.ExpenseEntity
import com.example.udhaarpay.data.local.entity.TransactionEntity
import com.example.udhaarpay.data.local.entity.UdhariRecordEntity
import com.example.udhaarpay.data.local.entity.UserProfileEntity
import com.example.udhaarpay.data.local.entity.WalletEntity

@Database(
    entities = [
        TransactionEntity::class,
        BankAccountEntity::class,
        CreditCardEntity::class,
        UdhariRecordEntity::class,
        UserProfileEntity::class,
        WalletEntity::class,
        ExpenseEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun bankAccountDao(): BankAccountDao
    abstract fun creditCardDao(): CreditCardDao
    abstract fun udhariDao(): UdhariDao
    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
}
