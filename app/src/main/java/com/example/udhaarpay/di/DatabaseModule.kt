package com.example.udhaarpay.di

import android.content.Context
import androidx.room.Room
import com.example.udhaarpay.data.AppDatabase
import com.example.udhaarpay.data.BankAccountDao
import com.example.udhaarpay.data.dao.RecurringPaymentDao
import com.example.udhaarpay.data.local.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "udhaarpay_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideRecurringPaymentDao(database: AppDatabase): RecurringPaymentDao {
        return database.recurringPaymentDao()
    }

    @Provides
    @Singleton
    fun provideBankAccountDao(database: AppDatabase): BankAccountDao {
        return database.bankAccountDao()
    }
}
