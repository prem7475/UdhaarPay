package com.example.udhaarpay.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Mock transaction data
val mockTransactions = listOf(
    Transaction("UPI Payment", "Paid to Rahul Sharma", "-₹1,200", "2026-01-02 14:23", "Success"),
    Transaction("Wallet Topup", "Added from HDFC Bank", "+₹5,000", "2026-01-01 10:12", "Success"),
    Transaction("Movie Ticket", "PVR Cinemas", "-₹650", "2025-12-30 19:45", "Success"),
    Transaction("Electricity Bill", "MSEB", "-₹1,800", "2025-12-28 09:30", "Failed"),
    Transaction("Credit Card Payment", "ICICI Bank", "-₹2,000", "2025-12-25 16:00", "Success"),
    Transaction("Received Money", "From Priya Singh", "+₹2,500", "2025-12-20 11:10", "Success"),
    Transaction("Bus Ticket", "RedBus", "-₹350", "2025-12-18 08:00", "Success"),
    Transaction("Mobile Recharge", "Airtel", "-₹299", "2025-12-15 13:20", "Success")
)
data class Transaction(val type: String, val description: String, val amount: String, val date: String, val status: String)

@Composable
fun TransactionsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Transaction History", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF2563EB))
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(mockTransactions) { tx ->
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
                            Text(tx.type, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(tx.description, fontSize = 13.sp, color = Color(0xFF64748B))
                            Text(tx.date, fontSize = 12.sp, color = Color(0xFF94A3B8))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(tx.amount, fontWeight = FontWeight.Bold, color = if (tx.amount.startsWith("+")) Color(0xFF059669) else Color(0xFFDC2626))
                            Text(tx.status, fontSize = 12.sp, color = if (tx.status == "Success") Color(0xFF059669) else Color(0xFFDC2626))
                        }
                    }
                }
            }
        }
    }
}
