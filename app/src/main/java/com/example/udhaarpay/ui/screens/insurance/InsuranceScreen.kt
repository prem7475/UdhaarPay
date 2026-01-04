package com.example.udhaarpay.ui.screens.insurance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val insuranceTypes = listOf("Health Insurance", "Life Insurance", "Car Insurance", "Travel Insurance", "Home Insurance")
val mockPolicies = listOf(
    Policy("Health Insurance", "Active", "₹5,00,000", "2026-01-01"),
    Policy("Car Insurance", "Expired", "₹2,00,000", "2024-12-31")
)
data class Policy(val type: String, val status: String, val coverage: String, val expiry: String)

@Composable
fun InsuranceScreen() {
    var selectedType by remember { mutableStateOf<String?>(null) }
    var showBuy by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Your Policies", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        LazyColumn {
            items(mockPolicies) { policy ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(policy.type, fontWeight = FontWeight.Bold)
                        Text("Status: ${policy.status}")
                        Text("Coverage: ${policy.coverage}")
                        Text("Expiry: ${policy.expiry}")
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Buy New Insurance", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        LazyColumn {
            items(insuranceTypes) { type ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { selectedType = type },
                ) {
                    Box(Modifier.padding(12.dp)) { Text(type) }
                }
            }
        }
        if (selectedType != null) {
            AlertDialog(
                onDismissRequest = { selectedType = null },
                title = { Text(selectedType!!) },
                text = { Text("Buy $selectedType for ₹5,000/year?\nCoverage: ₹5,00,000") },
                confirmButton = {
                    TextButton(onClick = {
                        showBuy = true
                        selectedType = null
                    }) { Text("Buy") }
                },
                dismissButton = { TextButton(onClick = { selectedType = null }) { Text("Cancel") } }
            )
        }
        if (showBuy) {
            AlertDialog(
                onDismissRequest = { showBuy = false },
                title = { Text("Purchase Successful") },
                text = { Text("Your insurance policy has been purchased.") },
                confirmButton = { TextButton(onClick = { showBuy = false }) { Text("OK") } }
            )
        }
    }
}
