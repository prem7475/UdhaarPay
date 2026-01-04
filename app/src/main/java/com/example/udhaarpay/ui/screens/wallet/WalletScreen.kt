package com.example.udhaarpay.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WalletScreen() {
    var balance by remember { mutableStateOf(10000.0) }
    var todaySpending by remember { mutableStateOf(500.0) }
    var monthSpending by remember { mutableStateOf(3500.0) }
    var showHistory by remember { mutableStateOf(false) }
    val transactions = listOf(
        WalletTransaction("Add Money", 2000.0, "1 Jan 2026"),
        WalletTransaction("Spent", -500.0, "2 Jan 2026"),
        WalletTransaction("Add Money", 1000.0, "3 Jan 2026")
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Wallet Balance", fontSize = 18.sp, color = Color(0xFF2563EB))
        Text("₹$balance", fontSize = 32.sp, color = Color(0xFF059669))
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Today's Spending: ₹$todaySpending", color = Color(0xFFDC2626))
            Text("Month's Spending: ₹$monthSpending", color = Color(0xFF7C3AED))
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { balance += 1000 }) { Text("Add Money") }
            Button(onClick = { if (balance >= 500) balance -= 500 }) { Text("Withdraw") }
            Button(onClick = { showHistory = !showHistory }) { Text("View History") }
        }
        Spacer(Modifier.height(16.dp))
        if (showHistory) {
            Text("Transaction History", fontSize = 18.sp, color = Color(0xFF111827))
            LazyColumn(modifier = Modifier.height(180.dp)) {
                items(transactions) { tx ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tx.type)
                            Text("₹${tx.amount}")
                            Text(tx.date)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        // Pie chart placeholder for categories breakdown
        Text("Categories Breakdown (Pie Chart)", color = Color(0xFF6B7280))
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
        ) {}
    }
}

data class WalletTransaction(val type: String, val amount: Double, val date: String)
