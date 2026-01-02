package com.example.udhaarpay.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.udhaarpay.MyApplication

class SharedViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            val app = application as MyApplication
            // Using lazy initialization - repositories will be injected via Hilt
            return SharedViewModel(app, null, null) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}