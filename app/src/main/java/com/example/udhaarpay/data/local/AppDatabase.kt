package com.example.udhaarpay.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.udhaarpay.data.local.dao.*
import com.example.udhaarpay.data.model.*

// REMOVED: WalletEntity (to prevent build errors if the file is missing)
@Database(
    entities = [
        Transaction::class,
        BankAccount::class,
        CreditCard::class,
        Udhari::class,
        User::class,
        Expense::class
    ],
    version = 2, // Increment version to trigger destructive migration
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun bankAccountDao(): BankAccountDao
    abstract fun creditCardDao(): CreditCardDao
    abstract fun udhariDao(): UdhariDao
    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
    
    // abstract fun walletDao(): WalletDao // Commented out until we create the Wallet model
}