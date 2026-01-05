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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(20.dp)
    ) {
        Text("Manage Wallets", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
        Spacer(Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mockWallets) { wallet ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (wallet.isDefault) Color(0xFF2563EB) else Color(0xFF1E293B)),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(wallet.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                            Text(wallet.balance, fontSize = 15.sp, color = Color(0xFFCBD5E1))
                        }
                        if (wallet.isDefault) {
                            Text("Default", color = Color(0xFF22C55E), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}