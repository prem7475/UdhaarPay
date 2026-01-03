package com.udhaarpay.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UdhaarPayHome(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun UdhaarPayHome(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1F1F1F))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "UdhaarPay",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Your Digital Payment & Loan Management App",
            fontSize = 16.sp,
            color = Color(0xFFB0B0B0),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Text(
            text = "App Status: Running",
            fontSize = 14.sp,
            color = Color(0xFF4CAF50),
            fontWeight = FontWeight.SemiBold
        )
        
        Text(
            text = "Build: 1.0.0",
            fontSize = 12.sp,
            color = Color(0xFF808080),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
