package com.udhaarpay.app.ui.screens.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Transactions", fontSize = 24.sp, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(mockTransactions) { txn ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(txn.description, fontSize = 16.sp)
                            Text(txn.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(txn.amount, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
