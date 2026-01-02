package com.example.udhaarpay.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.udhaarpay.MyApplication

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            val myApp = application as MyApplication
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel(myApp, null, null) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}