package com.example.udhaarpay.di

import android.content.Context
import androidx.room.Room
import com.example.udhaarpay.data.database.UdhaarPayDatabase
import com.example.udhaarpay.data.remote.PaymentApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.udhaarpay.com/v1/"

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Timber.tag("OkHttp").d(message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun providePaymentApiService(retrofit: Retrofit): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideUdhaarPayDatabase(
        @ApplicationContext context: Context
    ): UdhaarPayDatabase {
        return Room.databaseBuilder(
            context,
            UdhaarPayDatabase::class.java,
            "udhaarpay_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: UdhaarPayDatabase) = database.userDao()

    @Singleton
    @Provides
    fun provideCardDao(database: UdhaarPayDatabase) = database.cardDao()

    @Singleton
    @Provides
    fun provideBankAccountDao(database: UdhaarPayDatabase) = database.bankAccountDao()

    @Singleton
    @Provides
    fun provideTransactionDao(database: UdhaarPayDatabase) = database.transactionDao()

    @Singleton
    @Provides
    fun provideOfferDao(database: UdhaarPayDatabase) = database.offerDao()

    @Singleton
    @Provides
    fun provideServiceDao(database: UdhaarPayDatabase) = database.serviceDao()

    @Singleton
    @Provides
    fun provideTransactionCategoryDao(database: UdhaarPayDatabase) = database.transactionCategoryDao()

    @Singleton
    @Provides
    fun provideSpendingAnalyticsDao(database: UdhaarPayDatabase) = database.spendingAnalyticsDao()

    @Singleton
    @Provides
    fun provideQRScanDao(database: UdhaarPayDatabase) = database.qrScanDao()

    @Singleton
    @Provides
    fun provideNotificationDao(database: UdhaarPayDatabase) = database.notificationDao()
}
