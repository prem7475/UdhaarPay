package com.example.udhaarpay.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.data.local.entity.TransactionEntity

@Composable
fun HomeScreen(
    onNavigateToWallet: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToOffers: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = PureBlack,
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
                    amount = "₹${state.totalBalance}",
                    icon = Icons.Rounded.Wallet,
                    iconColor = PrimaryBlue,
                    iconBg = PrimaryBlue.copy(alpha = 0.1f)
                )

                // Credit Card Limit
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    title = "Credit Limit",
                    amount = "₹${state.creditLimit}",
                    icon = Icons.Default.CreditCard,
                    iconColor = Color(0xFFA855F7), // Purple
                    iconBg = Color(0x1AA855F7)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 2. QUICK ACTIONS ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    icon = Icons.Default.QrCodeScanner,
                    label = "Scan & Pay",
                    onClick = onNavigateToScan
                )
                QuickActionItem(
                    icon = Icons.Default.SwapHoriz,
                    label = "Transfer",
                    onClick = onNavigateToWallet
                )
                QuickActionItem(
                    icon = Icons.Default.CurrencyRupee, // Using Rupee as placeholder for Udhari
                    label = "Udhari",
                    onClick = onNavigateToTransactions
                )
                QuickActionItem(
                    icon = Icons.Default.GridView,
                    label = "All Services",
                    onClick = onNavigateToServices
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 3. RECENT TRANSACTIONS ---
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                color = White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Transaction List container
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Take remaining space
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkZinc)
                    .border(1.dp, Zinc800, RoundedCornerShape(16.dp))
            ) {
                if (state.transactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No transactions yet", color = Zinc400)
                        }
                    }
                } else {
                    items(state.transactions) { transaction ->
                        TransactionItem(transaction)
                        if (transaction != state.transactions.last()) {
                            HorizontalDivider(color = Zinc800, thickness = 1.dp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun HomeHeader(userName: String) {
    Column(modifier = Modifier.padding(vertical = 20.dp)) {
        Text("Welcome,", style = MaterialTheme.typography.titleMedium, color = Zinc400)
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = White
        )
    }
}

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: String,
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkZinc),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(title, style = MaterialTheme.typography.labelMedium, color = Zinc400)
            Spacer(modifier = Modifier.height(4.dp))
            Text(amount, style = MaterialTheme.typography.titleLarge, color = White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(DarkZinc, CircleShape)
                .border(1.dp, Zinc800, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Zinc400
        )
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    val isDebit = transaction.type.equals("Debit", ignoreCase = true)
    // Green for Credit, White for Debit as per requirements
    // Assuming Credit means money coming in (Positive), Debit means money going out (Negative/Neutral)
    // But requirement says: "Green text for Credit, White for Debit."
    
    val amountColor = if (!isDebit) Color.Green else White
    val iconVector = if (isDebit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    
    // Icon background: Red tint for debit, Green tint for credit usually, 
    // but sticking to a neutral or specific scheme. 
    // Let's use Zinc800 for background to keep it subtle on DarkZinc, or slightly lighter.
    // Or consistent with requirement: "Each item must have an icon (Arrow Up for Debit, Arrow Down for Credit)."
    
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
                    .background(Zinc800, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = if (!isDebit) Color.Green else White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(transaction.category, color = White, fontWeight = FontWeight.Medium)
                // Date formatting would go here, simplified for now
                Text(transaction.date.toString(), color = Zinc400, style = MaterialTheme.typography.labelSmall)
            }
        }

        Text(
            text = "${if (isDebit) "-" else "+"} ₹${transaction.amount}",
            color = amountColor,
            fontWeight = FontWeight.Bold
        )
    }
}
