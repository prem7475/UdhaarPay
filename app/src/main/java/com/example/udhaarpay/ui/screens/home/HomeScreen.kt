package com.example.udhaarpay.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
// import com.example.udhaarpay.ui.viewmodel.HomeViewModel

// Replace with your website's color scheme
val PrimaryColor = Color(0xFF2563EB)
val SecondaryColor = Color(0xFF7C3AED)
val AccentColor = Color(0xFF059669)
val BackgroundColor = Color(0xFFF9FAFB)
val TextPrimary = Color(0xFF111827)
val TextSecondary = Color(0xFF6B7280)

@Composable
fun HomeScreen(navController: NavController) {
    // TODO: Replace with real user data from ViewModel when available
    val userName = "Prem User"
    val walletBalance = 10000.0
    val quickActions = remember {
        listOf(
            QuickAction("send_money", "Send Money", "ic_send", PrimaryColor, "payments/send"),
            QuickAction("nfc_pay", "Scan & Pay", "ic_nfc", AccentColor, "payments/nfc"),
            QuickAction("tickets", "Tickets", "ic_ticket", SecondaryColor, "tickets"),
            QuickAction("invest", "Invest", "ic_invest", Color(0xFF22C55E), "investments"),
            QuickAction("insurance", "Insurance", "ic_insurance", Color(0xFF0EA5E9), "insurance"),
            QuickAction("credit_cards", "Credit Cards", "ic_card", Color(0xFFEC4899), "cards"),
            QuickAction("bill_payments", "Bill Payments", "ic_bill", Color(0xFFDC2626), "billpayments"),
            QuickAction("accounts", "My Accounts", "ic_account", Color(0xFF8B5CF6), "accounts")
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile image placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(SecondaryColor, shape = MaterialTheme.shapes.medium)
            ) {}
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Welcome, $userName", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
                Text("Wallet Balance: ₹$walletBalance", color = AccentColor, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(24.dp))
        // Quick Actions Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(320.dp)
        ) {
            items(quickActions) { action ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable { navController.navigate(action.route) },
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Replace with actual icons
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(action.color, shape = MaterialTheme.shapes.medium)
                        ) {}
                        Spacer(Modifier.height(8.dp))
                        Text(action.title, color = TextPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        // Recent Transactions (empty for now)
        Text("Recent Transactions", fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("No transactions yet.", color = TextSecondary)
        Spacer(Modifier.height(24.dp))
        // QR Code Section (placeholder)
        Text("Receive Money", fontWeight = FontWeight.Bold, color = TextPrimary)
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
        ) {
            // QR code placeholder
        }
        Spacer(Modifier.height(8.dp))
        Text("UPI ID: user@udhaarpay", color = TextSecondary)
    }
}

data class QuickAction(
    val id: String,
    val title: String,
    val icon: String, // Replace with actual icon resource
    val color: Color,
    val route: String
)
