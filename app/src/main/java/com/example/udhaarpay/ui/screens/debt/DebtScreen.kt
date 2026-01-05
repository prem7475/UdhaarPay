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
    var showPay by remember { mutableStateOf<Debt?>(null) }
    var showConfirm by remember { mutableStateOf(false) }
    var paidDebt by remember { mutableStateOf<Debt?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEF2F7))
            .padding(18.dp)
    ) {
        Text("Your Debts", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF1E293B))
        Spacer(Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                Modifier.padding(vertical = 18.dp, horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Outstanding", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Spacer(Modifier.weight(1f))
                val total = mockDebts.sumOf { it.amount.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0 }
                Text("₹" + "%,.0f".format(total), fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
            }
        }
        Spacer(Modifier.height(18.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mockDebts) { debt ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(debt.type, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val statusColor = when (debt.status) {
                                    "Active" -> Color(0xFF2563EB)
                                    "Overdue" -> Color(0xFFEF4444)
                                    "Paid" -> Color(0xFF22C55E)
                                    else -> Color.Gray
                                }
                                Text("Status: ", color = Color(0xFF64748B), fontSize = 14.sp)
                                Text(debt.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Text("Due: ${debt.dueDate}", color = Color(0xFF64748B), fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(debt.amount, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2563EB))
                            if (debt.status != "Paid") {
                                Button(
                                    onClick = { showPay = debt },
                                    modifier = Modifier.padding(top = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                                ) { Text("Pay Now", color = Color.White) }
                            } else {
                                Text("Paid", color = Color(0xFF22C55E), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
        if (showPay != null) {
            AlertDialog(
                onDismissRequest = { showPay = null },
                title = { Text("Pay Debt") },
                text = { Text("Pay your outstanding debt of ${showPay?.amount} for ${showPay?.type}?") },
                confirmButton = {
                    TextButton(onClick = {
                        paidDebt = showPay
                        showConfirm = true
                        showPay = null
                    }) { Text("Pay") }
                },
                dismissButton = { TextButton(onClick = { showPay = null }) { Text("Cancel") } }
            )
        }
        if (showConfirm && paidDebt != null) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Payment Successful") },
                text = { Text("Your payment for ${paidDebt?.type} has been processed.") },
                confirmButton = { TextButton(onClick = { showConfirm = false }) { Text("OK") } }
            )
        }
    }
}
