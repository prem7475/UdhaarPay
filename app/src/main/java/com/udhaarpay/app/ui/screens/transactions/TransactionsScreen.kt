package com.udhaarpay.app.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Mock data
val mockTransactions = listOf(
    TransactionItem("UPI Payment", "-₹500", "2026-01-01"),
    TransactionItem("Bank Transfer", "+₹2,000", "2025-12-30"),
    TransactionItem("Recharge", "-₹199", "2025-12-28")
)

data class TransactionItem(val description: String, val amount: String, val date: String)

@Composable
fun TransactionsScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(20.dp)
    ) {
        Text(
            "Transactions",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2563EB),
            modifier = Modifier.padding(bottom = 10.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mockTransactions) { txn ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    shadowElevation = 4.dp,
                    color = Color(0xFF1E293B)
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(txn.description, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(txn.date, fontSize = 12.sp, color = Color(0xFFCBD5E1))
                        }
                        Text(
                            txn.amount,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (txn.amount.startsWith("-")) Color(0xFFDC2626) else Color(0xFF22C55E)
                        )
                    }
                }
            }
        }
    }
}
