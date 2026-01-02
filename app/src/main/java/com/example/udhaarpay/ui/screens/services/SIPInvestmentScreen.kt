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
fun SIPInvestmentScreen(
    onBack: () -> Unit
) {
    var amount by remember { mutableStateOf(1000.toString()) }
    var frequency by remember { mutableStateOf("Monthly") }
    var tenureYears by remember { mutableStateOf(5) }

    Surface(modifier = Modifier.padding(16.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("SIP Investment", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() } },
                label = { Text("Monthly SIP Amount (₹)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = frequency,
                onValueChange = { frequency = it },
                label = { Text("Frequency") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Tenure: $tenureYears years")

            Button(onClick = { /* Simple placeholder: calculate projected value */ }) {
                Text("Estimate Returns")
            }

            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
