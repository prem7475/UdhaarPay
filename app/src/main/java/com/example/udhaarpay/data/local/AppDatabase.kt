package com.example.udhaarpay.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.udhaarpay.data.local.dao.*
import com.example.udhaarpay.data.local.entity.WalletEntity
import com.example.udhaarpay.data.model.*

@Database(
    entities = [
        Transaction::class,
        BankAccount::class,
        CreditCard::class,
        Udhari::class,
        User::class,
        Expense::class,
        WalletEntity::class
    ],
    version = 1,
    exportSchema = false
)
// NO @TypeConverters HERE! (We removed them)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun bankAccountDao(): BankAccountDao
    abstract fun creditCardDao(): CreditCardDao
    abstract fun udhariDao(): UdhariDao
    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun walletDao(): WalletDao
}