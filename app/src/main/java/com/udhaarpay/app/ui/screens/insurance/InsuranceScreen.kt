package com.udhaarpay.app.ui.screens.insurance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.local.entities.Insurance
import com.udhaarpay.app.ui.viewmodel.InsuranceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InsuranceScreen(viewModel: InsuranceViewModel = hiltViewModel()) {
    val policies by viewModel.insurances.collectAsState()
    var showApply by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Insurance", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { showApply = true }) { Text("Apply Policy") }
        }
        Spacer(Modifier.height(10.dp))

        if (policies.isEmpty()) {
            Text("No policies yet.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(policies) { policy ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(policy.policyType, fontWeight = FontWeight.SemiBold)
                                Text("Provider: ${policy.provider}")
                                Text("Premium: INR ${policy.premium}")
                                Text(
                                    "Expiry: ${
                                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(policy.expiryDate))
                                    }",
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                policy.status.replaceFirstChar { it.uppercase() },
                                color = if (policy.status.lowercase() == "active") Color(0xFF22C55E) else Color(0xFFF59E0B)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showApply) {
        ApplyInsuranceDialog(
            onDismiss = { showApply = false },
            onApply = { type, provider, premium, coverage ->
                val now = System.currentTimeMillis()
                viewModel.insert(
                    Insurance(
                        policyType = type,
                        provider = provider,
                        premium = premium,
                        startDate = now,
                        expiryDate = now + (365L * 24 * 60 * 60 * 1000),
                        status = "active",
                        coverage = coverage
                    )
                )
                showApply = false
            }
        )
    }
}

@Composable
private fun ApplyInsuranceDialog(
    onDismiss: () -> Unit,
    onApply: (String, String, Double, String) -> Unit
) {
    var type by remember { mutableStateOf("Health Insurance") }
    var provider by remember { mutableStateOf("HDFC Ergo") }
    var premium by remember { mutableStateOf("999") }
    var coverage by remember { mutableStateOf("INR 10 Lakh") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Apply Insurance") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") })
                OutlinedTextField(value = provider, onValueChange = { provider = it }, label = { Text("Provider") })
                OutlinedTextField(
                    value = premium,
                    onValueChange = { premium = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Premium") }
                )
                OutlinedTextField(value = coverage, onValueChange = { coverage = it }, label = { Text("Coverage") })
                if (!error.isNullOrBlank()) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val premiumValue = premium.toDoubleOrNull()
                when {
                    type.isBlank() -> error = "Type is required"
                    provider.isBlank() -> error = "Provider is required"
                    premiumValue == null || premiumValue <= 0.0 -> error = "Premium is invalid"
                    else -> onApply(type, provider, premiumValue, coverage)
                }
            }) {
                Text("Apply")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
