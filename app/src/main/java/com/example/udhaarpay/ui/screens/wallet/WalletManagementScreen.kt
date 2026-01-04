package com.example.udhaarpay.ui.screens.wallet

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

val mockWallets = listOf(
    Wallet("Main Wallet", "₹10,000", true),
    Wallet("Travel Wallet", "₹2,500", false),
    Wallet("Shopping Wallet", "₹1,200", false)
)
data class Wallet(val name: String, val balance: String, val isDefault: Boolean)

@Composable
fun WalletManagementScreen() {
    var showAdd by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    var showSetDefault by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Manage Wallets", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        LazyColumn {
            items(mockWallets) { wallet ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                ) {
                    Row(
                        Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(wallet.name, fontWeight = FontWeight.Bold)
                            Text("Balance: ${wallet.balance}")
                        }
                        if (!wallet.isDefault) {
                            Button(onClick = { showSetDefault = true }) { Text("Set Default") }
                        } else {
                            Text("Default", color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { showAdd = true }, modifier = Modifier.fillMaxWidth()) { Text("Add New Wallet") }
        if (showAdd) {
            AlertDialog(
                onDismissRequest = { showAdd = false },
                title = { Text("Add Wallet") },
                text = { Text("Add a new wallet to your account?") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirm = true
                        showAdd = false
                    }) { Text("Add") }
                },
                dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Cancel") } }
            )
        }
        if (showSetDefault) {
            AlertDialog(
                onDismissRequest = { showSetDefault = false },
                title = { Text("Set Default Wallet") },
                text = { Text("Set this wallet as your default?") },
                confirmButton = {
                    TextButton(onClick = {
                        showSetDefault = false
                        showConfirm = true
                    }) { Text("Set Default") }
                },
                dismissButton = { TextButton(onClick = { showSetDefault = false }) { Text("Cancel") } }
            )
        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Success") },
                text = { Text("Wallet action completed.") },
                confirmButton = { TextButton(onClick = { showConfirm = false }) { Text("OK") } }
            )
        }
    }
}
