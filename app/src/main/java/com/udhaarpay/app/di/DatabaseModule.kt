package com.udhaarpay.app.di

import android.content.Context
import androidx.room.Room
import com.udhaarpay.app.data.local.AppDatabase
import com.udhaarpay.app.data.local.dao.BankAccountDao
import com.udhaarpay.app.data.local.dao.CreditCardDao
import com.udhaarpay.app.data.local.dao.DebtDao
import com.udhaarpay.app.data.local.dao.ExpenseDao
import com.udhaarpay.app.data.local.dao.InsuranceDao
import com.udhaarpay.app.data.local.dao.InvestmentDao
import com.udhaarpay.app.data.local.dao.TicketDao
import com.udhaarpay.app.data.local.dao.UPIPaymentDao
import com.udhaarpay.app.data.local.dao.UserProfileDao
import com.udhaarpay.app.data.local.dao.NFCTransactionDao
import com.udhaarpay.app.data.local.dao.PaperTradingDao
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
            "udhaarpay_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(appDatabase: AppDatabase): UserProfileDao {
        return appDatabase.userProfileDao()
    }

    @Provides
    @Singleton
    fun provideUpiPaymentDao(appDatabase: AppDatabase): UPIPaymentDao {
        return appDatabase.upiPaymentDao()
    }

    @Provides
    @Singleton
    fun provideDebtDao(appDatabase: AppDatabase): DebtDao {
        return appDatabase.debtDao()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(appDatabase: AppDatabase): ExpenseDao {
        return appDatabase.expenseDao()
    }

    @Provides
    @Singleton
    fun provideTicketDao(appDatabase: AppDatabase): TicketDao {
        return appDatabase.ticketDao()
    }

    @Provides
    @Singleton
    fun provideInvestmentDao(appDatabase: AppDatabase): InvestmentDao {
        return appDatabase.investmentDao()
    }

    @Provides
    @Singleton
    fun provideInsuranceDao(appDatabase: AppDatabase): InsuranceDao {
        return appDatabase.insuranceDao()
    }

    @Provides
    @Singleton
    fun provideCreditCardDao(appDatabase: AppDatabase): CreditCardDao {
        return appDatabase.creditCardDao()
    }

    @Provides
    @Singleton
    fun provideBankAccountDao(appDatabase: AppDatabase): BankAccountDao {
        return appDatabase.bankAccountDao()
    }

    @Provides
    @Singleton
    fun provideNfcTransactionDao(appDatabase: AppDatabase): NFCTransactionDao {
        return appDatabase.nfcTransactionDao()
    }

    @Provides
    @Singleton
    fun providePaperTradingDao(appDatabase: AppDatabase): PaperTradingDao {
        return appDatabase.paperTradingDao()
    }
}
