package com.example.udhaarpay.data.recurring

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import timber.log.Timber

class RecurringPaymentWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            Timber.d("Recurring payment worker executed")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error in recurring payment worker")
            Result.retry()
        }
    }
}
