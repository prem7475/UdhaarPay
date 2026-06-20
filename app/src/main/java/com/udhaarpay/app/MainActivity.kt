package com.udhaarpay.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.udhaarpay.app.core.MockDataSeeder
import com.udhaarpay.app.ui.navigation.UdhaarPayAppRoot
import com.udhaarpay.app.ui.theme.UdhaarPayTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var mockDataSeeder: MockDataSeeder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UdhaarPayTheme {
                UdhaarPayAppRoot(seeder = mockDataSeeder)
            }
        }
    }
}
