package com.udhaarpay.app.ui.screens.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun RemindersScreen(content: @Composable () -> Unit = {}) {
    var reminders by remember { mutableStateOf(mockReminders) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(20.dp)
    ) {
        Text(
            "Bill Reminders",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color(0xFF2563EB),
            modifier = Modifier.padding(bottom = 10.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(reminders) { reminder ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 6.dp,
                    color = Color(0xFF1E293B)
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(reminder.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                            Text("Due: ${reminder.dueDate}", fontSize = 13.sp, color = Color(0xFFCBD5E1))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(reminder.amount, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF22C55E))
                            Text(
                                if (reminder.paid) "Paid" else "Unpaid",
                                fontSize = 12.sp,
                                color = if (reminder.paid) Color(0xFF22C55E) else Color(0xFFDC2626),
                                modifier = Modifier.background(
                                    color = if (reminder.paid) Color(0xFF16213E) else Color(0xFF3B1F1F),
                                    shape = RoundedCornerShape(8.dp)
                                ).padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}