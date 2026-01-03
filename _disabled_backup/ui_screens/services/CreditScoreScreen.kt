package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreditScoreScreen(
    onBack: () -> Unit
) {
    val placeholderScore = remember { 680 }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Credit Score Check", style = MaterialTheme.typography.headlineSmall)

        Text("Estimated CIBIL Score: $placeholderScore", style = MaterialTheme.typography.bodyLarge)

        Text("This is a placeholder estimator — integrate with credit bureaus for live data.")

        Button(onClick = { /* Placeholder: link to detailed report */ }) {
            Text("View Detailed Report")
        }

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
