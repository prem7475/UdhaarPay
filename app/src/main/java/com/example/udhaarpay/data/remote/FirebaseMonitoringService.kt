package com.example.udhaarpay.data.remote

import com.example.udhaarpay.data.model.Transaction
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class FirebaseMonitoringService @Inject constructor() {
    fun logTransactionEvent(eventName: String, transaction: Transaction) {
        // Lightweight stub for analytics/monitoring telemetry
        Timber.d("[FirebaseMonitoring] $eventName -> ${transaction.transactionId}")
    }
}
