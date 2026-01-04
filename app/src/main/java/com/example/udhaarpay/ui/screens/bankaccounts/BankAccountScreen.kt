package com.example.udhaarpay.ui.screens.bankaccounts

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

val mockAccounts = listOf(
    BankAccount("HDFC Bank", "Savings", "**** 9876", "₹1,50,000"),
    BankAccount("ICICI Bank", "Current", "**** 4321", "₹2,30,000")
)
data class BankAccount(val bank: String, val type: String, val number: String, val balance: String)

@Composable
fun BankAccountScreen() {
    var showAdd by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Linked Bank Accounts", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        LazyColumn {
            items(mockAccounts) { acc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(acc.bank, fontWeight = FontWeight.Bold)
                        Text("Type: ${acc.type}")
                        Text("Number: ${acc.number}")
                        Text("Balance: ${acc.balance}")
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { showAdd = true }, modifier = Modifier.fillMaxWidth()) { Text("Add New Account") }
        if (showAdd) {
            AlertDialog(
                onDismissRequest = { showAdd = false },
                title = { Text("Add Bank Account") },
                text = { Text("Add a new bank account to your profile?") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirm = true
                        showAdd = false
                    }) { Text("Add") }
                },
                dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Cancel") } }
            )
        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Account Added") },
                text = { Text("Your new bank account has been linked.") },
                confirmButton = { TextButton(onClick = { showConfirm = false }) { Text("OK") } }
            )
        }
    }
}
