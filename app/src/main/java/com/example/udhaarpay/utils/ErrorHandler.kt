package com.example.udhaarpay.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor() {
    fun handleError(throwable: Throwable, context: String = "") {
        // Global error handling logic
        if (context.isNotEmpty()) {
            android.util.Log.e("ErrorHandler", "Error in $context: ${throwable.message}", throwable)
        } else {
            android.util.Log.e("ErrorHandler", throwable.message ?: "Unknown error", throwable)
        }
    }
}
