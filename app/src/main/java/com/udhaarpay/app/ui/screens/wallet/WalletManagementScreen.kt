package com.udhaarpay.app.ui.screens.wallet

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
                    // ...existing code...
                }
            }
        }
    }
}