package com.udhaarpay.app.di

import android.content.Context
import androidx.room.Room
import com.udhaarpay.app.data.local.AppDatabase
import com.udhaarpay.app.data.local.dao.CreditCardDao
import com.udhaarpay.app.data.local.dao.BankAccountDao
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
        ).build()
    }

   @Provides
    fun provideCreditCardDao(appDatabase: AppDatabase): CreditCardDao {
        return appDatabase.creditCardDao()
    }

    @Provides
    fun provideBankAccountDao(appDatabase: AppDatabase): BankAccountDao {
        return appDatabase.bankAccountDao()
    }
}
