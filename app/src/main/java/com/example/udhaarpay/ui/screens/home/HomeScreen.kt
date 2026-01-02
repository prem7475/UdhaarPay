package com.udhaarpay.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.presentation.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel() // Assuming you have this set up
) {
    // Collect state from your ViewModel (adapt to your actual HomeState)
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Black, // Global Background
        topBar = { HomeHeader(userName = "Prem Narayani") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {

            // --- 1. DASHBOARD CARDS ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Bank Balance Card
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Balance",
                    amount = "₹50,000", // Replace with state.totalBalance
                    icon = Icons.Rounded.Wallet,
                    iconColor = Blue600,
                    iconBg = Blue500_10
                )

                // Credit Card Limit
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    title = "Credit Limit",
                    amount = "₹1,20,000", // Replace with state.creditLimit
                    icon = Icons.Default.CreditCard,
                    iconColor = Color(0xFFA855F7), // Purple
                    iconBg = Color(0x1AA855F7)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 2. RECENT TRANSACTIONS ---
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                color = White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Transaction List container matching website's "Zinc-900 with Border" look
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Zinc900)
                    .border(1.dp, Zinc800, RoundedCornerShape(16.dp))
            ) {
                // If empty state
                if (state.transactions.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No transactions yet", color = Zinc500)
                        }
                    }
                } else {
                    items(state.transactions) { transaction ->
                        TransactionItem(transaction)
                        // Divider between items except the last one
                        if (transaction != state.transactions.last()) {
                            Divider(color = Zinc800, thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun HomeHeader(userName: String) {
    Column(modifier = Modifier.padding(20.dp)) {
        Text("Welcome,", style = MaterialTheme.typography.titleMedium, color = Zinc400)
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            brush = Brush.horizontalGradient(listOf(Blue600, Blue700)) // Gradient Text effect
        )
    }
}

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    iconBg: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Zinc900),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(title, style = MaterialTheme.typography.labelMedium, color = Zinc500)
            Spacer(modifier = Modifier.height(4.dp))
            Text(amount, style = MaterialTheme.typography.titleLarge, color = White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TransactionItem(transaction: com.udhaarpay.app.domain.model.Transaction) {
    val isDebit = transaction.type == "debit"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(if (isDebit) Red500_10 else Green500_10, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDebit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (isDebit) Red500 else Green500,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(transaction.name, color = White, fontWeight = FontWeight.Medium)
                Text(transaction.date, color = Zinc500, style = MaterialTheme.typography.labelSmall)
            }
        }

        Text(
            text = "${if (isDebit) "-" else "+"} ₹${transaction.amount}",
            color = if (isDebit) White else Green500,
            fontWeight = FontWeight.Bold
        )
    }
}