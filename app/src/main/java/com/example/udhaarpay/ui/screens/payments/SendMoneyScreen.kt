package com.example.udhaarpay.ui.screens.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyScreen() {
    var recipient by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    val contacts = listOf("Rajesh Kumar", "Priya Sharma", "Amit Singh", "Sarah Johnson")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Send Money", fontSize = 22.sp, color = Color(0xFF2563EB))
        Spacer(Modifier.height(16.dp))
        // Recipient input (mock contact list)
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = {}
        ) {
            OutlinedTextField(
                value = recipient,
                onValueChange = { recipient = it },
                label = { Text("Recipient") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            onClick = { showSuccess = true },
            enabled = recipient.isNotBlank() && amount.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Send") }
        if (showSuccess) {
            AlertDialog(
                onDismissRequest = { showSuccess = false },
                title = { Text("Success") },
                text = { Text("₹$amount sent to $recipient!") },
                confirmButton = {
                    TextButton(onClick = { showSuccess = false }) { Text("OK") }
                }
            )
        }
    }
}
