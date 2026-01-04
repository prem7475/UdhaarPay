package com.example.udhaarpay.ui.screens.creditcards

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

val mockCards = listOf(
    CreditCard("Visa Platinum", "**** 1234", "Active", "₹1,20,000", "2028-12"),
    CreditCard("Mastercard Gold", "**** 5678", "Blocked", "₹80,000", "2026-09")
)
data class CreditCard(val name: String, val number: String, val status: String, val limit: String, val expiry: String)

@Composable
fun CreditCardScreen() {
    var showApply by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Your Credit Cards", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        LazyColumn {
            items(mockCards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(card.name, fontWeight = FontWeight.Bold)
                        Text("Number: ${card.number}")
                        Text("Status: ${card.status}")
                        Text("Limit: ${card.limit}")
                        Text("Expiry: ${card.expiry}")
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { showApply = true }, modifier = Modifier.fillMaxWidth()) { Text("Apply for New Card") }
        if (showApply) {
            AlertDialog(
                onDismissRequest = { showApply = false },
                title = { Text("Apply for Credit Card") },
                text = { Text("Apply for a new credit card with a limit up to ₹2,00,000?") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirm = true
                        showApply = false
                    }) { Text("Apply") }
                },
                dismissButton = { TextButton(onClick = { showApply = false }) { Text("Cancel") } }
            )
        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Application Submitted") },
                text = { Text("Your credit card application has been submitted.") },
                confirmButton = { TextButton(onClick = { showConfirm = false }) { Text("OK") } }
            )
        }
    }
}
