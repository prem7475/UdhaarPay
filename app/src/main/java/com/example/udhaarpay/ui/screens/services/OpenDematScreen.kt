package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OpenDematScreen(
    onBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Surface(modifier = Modifier.padding(16.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Open Demat Account", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it.filter { ch -> ch.isDigit() } },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { /* Placeholder: start onboarding flow */ }) {
                Text("Start Onboarding")
            }

            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
