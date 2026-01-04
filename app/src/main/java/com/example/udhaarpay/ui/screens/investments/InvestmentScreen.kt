package com.example.udhaarpay.ui.screens.investments

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

val brokers = listOf(
    "Zerodha", "Groww", "5Paisa", "Upstox", "Angel Broking", "ICICI Direct", "Kuvera", "ET Money"
)
val funds = listOf(
    "Large Cap Fund", "Mid Cap Fund", "Small Cap Fund", "Balanced Fund", "Index Fund"
)

@Composable
fun InvestmentScreen() {
    var selectedBroker by remember { mutableStateOf<String?>(null) }
    var selectedFund by remember { mutableStateOf<String?>(null) }
    var amount by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Monthly") }
    var showConfirm by remember { mutableStateOf(false) }
    val frequencies = listOf("Monthly", "Quarterly", "Annual")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        when {
            selectedBroker == null -> {
                Text("Select Broker", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                LazyColumn {
                    items(brokers) { broker ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { selectedBroker = broker },
                        ) {
                            Box(Modifier.padding(12.dp)) { Text(broker) }
                        }
                    }
                }
            }
            selectedFund == null -> {
                Text("Select Fund", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                LazyColumn {
                    items(funds) { fund ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { selectedFund = fund },
                        ) {
                            Box(Modifier.padding(12.dp)) { Text(fund) }
                        }
                    }
                }
            }
            else -> {
                Text("SIP Details", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹500 min)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Frequency:", modifier = Modifier.padding(end = 8.dp))
                    frequencies.forEach { freq ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { frequency = freq }.padding(end = 8.dp)
                        ) {
                            RadioButton(selected = frequency == freq, onClick = { frequency = freq })
                            Text(freq)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { showConfirm = true },
                    enabled = amount.toIntOrNull() ?: 0 >= 500,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Start SIP") }
                if (showConfirm) {
                    AlertDialog(
                        onDismissRequest = { showConfirm = false },
                        title = { Text("Investment Confirmed") },
                        text = { Text("Broker: $selectedBroker\nFund: $selectedFund\nAmount: ₹$amount\nFrequency: $frequency") },
                        confirmButton = {
                            TextButton(onClick = {
                                selectedBroker = null
                                selectedFund = null
                                amount = ""
                                frequency = "Monthly"
                                showConfirm = false
                            }) { Text("OK") }
                        }
                    )
                }
            }
        }
    }
}
