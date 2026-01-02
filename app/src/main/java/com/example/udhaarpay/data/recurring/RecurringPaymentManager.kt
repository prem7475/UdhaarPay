package com.example.udhaarpay.data.recurring

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurringPaymentManager @Inject constructor() {

    suspend fun executeRecurringPayment(_paymentId: String): Boolean {
        // TODO: Implement recurring payment logic
        // For now, return true
        return true
    }
}
