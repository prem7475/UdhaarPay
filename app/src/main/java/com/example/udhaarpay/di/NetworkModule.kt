package com.example.udhaarpay.di

import com.example.udhaarpay.data.remote.FirebaseMonitoringService
import com.example.udhaarpay.data.remote.FirebaseRealtimeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

	@Singleton
	@Provides
	fun provideErrorHandler(): com.example.udhaarpay.utils.ErrorHandler {
		return com.example.udhaarpay.utils.ErrorHandler()
	}

    @Singleton
    @Provides
    fun provideFirebaseMonitoringService(): FirebaseMonitoringService {
        return FirebaseMonitoringService()
    }	@Singleton
	@Provides
	fun provideFirebaseRealtimeService(errorHandler: com.example.udhaarpay.utils.ErrorHandler): FirebaseRealtimeService {
		return FirebaseRealtimeService(errorHandler)
	}
}
