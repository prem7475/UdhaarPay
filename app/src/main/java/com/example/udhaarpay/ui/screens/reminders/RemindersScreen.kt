package com.example.udhaarpay.ui.screens.reminders

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

val mockReminders = listOf(
    Reminder("Electricity Bill", "₹1,800", "2026-01-10", false),
    Reminder("Credit Card Payment", "₹2,000", "2026-01-15", false),
    Reminder("Mobile Recharge", "₹299", "2026-01-05", true),
    Reminder("DTH Recharge", "₹450", "2026-01-12", false)
)
data class Reminder(val title: String, val amount: String, val dueDate: String, val paid: Boolean)

@Composable
fun RemindersScreen() {
    var reminders by remember { mutableStateOf(mockReminders) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Bill Reminders", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF6366F1))
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(reminders) { reminder ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(reminder.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Due: ${reminder.dueDate}", fontSize = 13.sp, color = Color(0xFF64748B))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(reminder.amount, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                            if (reminder.paid) {
                                Text("Paid", fontSize = 12.sp, color = Color(0xFF059669))
                            } else {
                                Button(onClick = { /* TODO: Mark as paid */ }) {
                                    Text("Pay Now")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
