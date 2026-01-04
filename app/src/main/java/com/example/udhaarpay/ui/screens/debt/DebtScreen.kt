package com.example.udhaarpay.ui.screens.debt

import androidx.compose.foundation.background
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

val mockDebts = listOf(
    Debt("Personal Loan", "Active", "₹50,000", "2027-05-01"),
    Debt("Credit Card Dues", "Overdue", "₹12,000", "2025-01-15")
)
data class Debt(val type: String, val status: String, val amount: String, val dueDate: String)

@Composable
fun DebtScreen() {
    var showPay by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Your Debts", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        LazyColumn {
            items(mockDebts) { debt ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(debt.type, fontWeight = FontWeight.Bold)
                        Text("Status: ${debt.status}")
                        Text("Amount: ${debt.amount}")
                        Text("Due: ${debt.dueDate}")
                        if (debt.status != "Paid") {
                            Button(onClick = { showPay = true }, modifier = Modifier.padding(top = 8.dp)) { Text("Pay Now") }
                        }
                    }
                }
            }
        }
        if (showPay) {
            AlertDialog(
                onDismissRequest = { showPay = false },
                title = { Text("Pay Debt") },
                text = { Text("Pay your outstanding debt now?") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirm = true
                        showPay = false
                    }) { Text("Pay") }
                },
                dismissButton = { TextButton(onClick = { showPay = false }) { Text("Cancel") } }
            )
        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Payment Successful") },
                text = { Text("Your debt payment has been processed.") },
                confirmButton = { TextButton(onClick = { showConfirm = false }) { Text("OK") } }
            )
        }
    }
}
