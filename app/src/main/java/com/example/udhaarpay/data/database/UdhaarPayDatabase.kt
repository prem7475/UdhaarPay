package com.example.udhaarpay.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.udhaarpay.data.local.converter.DateConverter
import com.example.udhaarpay.data.local.converter.TransactionTypeConverter
import com.example.udhaarpay.data.local.converter.EnumConverters
import com.example.udhaarpay.data.local.converter.AccountTypeConverter
import com.example.udhaarpay.data.model.*

@Database(
    entities = [
        User::class,
        BankCard::class,
        BankAccount::class,
        Transaction::class,
        Offer::class,
        Service::class,
        TransactionCategory::class,
        SpendingAnalytics::class,
        QRScan::class,
        Notification::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, TransactionTypeConverter::class, EnumConverters::class, AccountTypeConverter::class)
abstract class UdhaarPayDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cardDao(): CardDao
    abstract fun bankAccountDao(): BankAccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun offerDao(): OfferDao
    abstract fun serviceDao(): ServiceDao
    abstract fun transactionCategoryDao(): TransactionCategoryDao
    abstract fun spendingAnalyticsDao(): SpendingAnalyticsDao
    abstract fun qrScanDao(): QRScanDao
    abstract fun notificationDao(): NotificationDao
}
