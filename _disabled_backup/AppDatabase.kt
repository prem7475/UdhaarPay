package com.example.udhaarpay.data.obsolete

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.udhaarpay.data.dao.RecurringPaymentDao
import com.example.udhaarpay.data.local.TransactionDao
import com.example.udhaarpay.data.local.converter.DateConverter
import com.example.udhaarpay.data.local.converter.EnumConverters
import com.example.udhaarpay.data.local.converter.TransactionTypeConverter
import com.example.udhaarpay.data.model.AccountType
import com.example.udhaarpay.data.model.BankAccount
import com.example.udhaarpay.data.model.RecurringPayment
import com.example.udhaarpay.data.model.Transaction

class AccountTypeConverter {
    @TypeConverter
    fun fromAccountType(value: String?) = value?.let { AccountType.valueOf(it) }
    @TypeConverter
    fun toAccountType(type: AccountType?) = type?.name
}

// This file was moved to an obsolete package to avoid duplicate Room database definitions.
// The real AppDatabase lives in `com.example.udhaarpay.data.local`.
abstract class ObsoleteAppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun bankAccountDao(): com.example.udhaarpay.data.BankAccountDao // Corrected Path
    abstract fun recurringPaymentDao(): RecurringPaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "udhaarpay_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
