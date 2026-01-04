package com.udhaarpay.app.ui.screens.bankaccounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

val mockAccounts = listOf(
    BankAccount("HDFC Bank", "Savings", "**** 9876", "₹1,50,000"),
    BankAccount("ICICI Bank", "Current", "**** 4321", "₹2,30,000")
)
data class BankAccount(val bank: String, val type: String, val number: String, val balance: String)

@Composable
fun BankAccountScreen() {
    var showAdd by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    // Calculate total balance (remove non-numeric chars)
    val totalBalance = mockAccounts.sumOf {
        it.balance.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(20.dp)
    ) {
        Text("Linked Bank Accounts", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
        Spacer(Modifier.height(10.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            color = Color(0xFF2563EB),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.padding(vertical = 18.dp, horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Balance", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Spacer(Modifier.weight(1f))
                Text("₹" + "%,.0f".format(totalBalance), fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
            }
        }
        Spacer(Modifier.height(18.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mockAccounts) { acc ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(acc.bank, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                            Text(acc.type + " • " + acc.number, fontSize = 15.sp, color = Color(0xFFCBD5E1))
                        }
                        Text(acc.balance, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF22C55E))
                    }
                }
            }
        }
    }
}