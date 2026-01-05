package com.udhaarpay.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.udhaarpay.app.data.local.dao.CreditCardDao
import com.udhaarpay.app.data.local.dao.BankAccountDao
import com.udhaarpay.app.data.local.entities.CreditCard
import com.udhaarpay.app.data.local.entities.BankAccount

@Database(entities = [CreditCard::class, BankAccount::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun creditCardDao(): CreditCardDao
    abstract fun bankAccountDao(): BankAccountDao
}
