package com.udhaarpay.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.udhaarpay.app.ui.navigation.UdhaarPayAppShell
import com.udhaarpay.app.ui.theme.UdhaarPayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UdhaarPayTheme {
                UdhaarPayAppShell()
            }
        }
    }
}
