package com.example.udhaarpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.udhaarpay.ui.navigation.AppNavigation
import com.example.udhaarpay.ui.theme.UdhaarPayTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        enableEdgeToEdge()
        
        setContent {
            UdhaarPayTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    AppNavigation()
                }
            }
        }
    }
}
