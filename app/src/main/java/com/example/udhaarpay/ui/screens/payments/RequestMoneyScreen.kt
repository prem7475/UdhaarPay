package com.example.udhaarpay.ui.screens.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RequestMoneyScreen() {
    var requester by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var showRequested by remember { mutableStateOf(false) }
    var pendingRequests by remember { mutableStateOf(listOf("Priya Sharma - ₹500", "Amit Singh - ₹1000")) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Request Money", fontSize = 22.sp, color = Color(0xFF2563EB))
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = requester,
            onValueChange = { requester = it },
            label = { Text("Requester Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                pendingRequests = listOf("$requester - ₹$amount") + pendingRequests
                showRequested = true
            },
            enabled = requester.isNotBlank() && amount.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Request") }
        if (showRequested) {
            AlertDialog(
                onDismissRequest = { showRequested = false },
                title = { Text("Request Sent") },
                text = { Text("Requested ₹$amount from $requester!") },
                confirmButton = {
                    TextButton(onClick = { showRequested = false }) { Text("OK") }
                }
            )
        }
        Spacer(Modifier.height(24.dp))
        Text("Pending Requests", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        pendingRequests.forEach { req ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(req)
                    Button(onClick = { /* Mark as received */ }) { Text("Mark Received") }
                }
            }
        }
    }
}
