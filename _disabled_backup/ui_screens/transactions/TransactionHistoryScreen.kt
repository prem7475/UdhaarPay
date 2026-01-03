package com.example.udhaarpay.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.data.model.Transaction
import com.example.udhaarpay.data.model.TransactionType
import com.example.udhaarpay.data.model.TransactionStatus
import com.example.udhaarpay.ui.components.PremiumTopAppBar
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionHistoryScreen(
    onBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Food", "Transport", "Health", "Shopping", "Bills", "Travel", "Entertainment", "Other")
    val filteredTransactions = if (selectedCategory == "All") {
        transactions
    } else {
        transactions.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground)
    ) {
        PremiumTopAppBar(
            title = "Transaction History",
            onBackClick = onBack
        )

        // Category Filter
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonOrange,
                        selectedLabelColor = DarkBackground,
                        containerColor = DarkCard,
                        labelColor = TextSecondary
                    ),
                    border = if (selectedCategory == category) null else FilterChipDefaults.border(
                        borderColor = TextTertiary.copy(alpha = 0.3f)
                    )
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonOrange)
            }
        } else if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "No transactions",
                        tint = TextTertiary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No transactions yet",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 0.dp)
            ) {
                items(filteredTransactions.groupBy { getDateKey(it.timestamp.time) }.toList()) { (dateKey, dayTransactions) ->
                    TransactionGroup(dateKey = dateKey, transactions = dayTransactions)
                }
            }
        }
    }
}

@Composable
fun TransactionGroup(dateKey: String, transactions: List<Transaction>) {
    Column {
        Text(
            text = dateKey,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        transactions.forEach { transaction ->
            TransactionItemCard(transaction = transaction)
        }
    }
}

@Composable
fun TransactionItemCard(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            color = when (transaction.type) {
                                TransactionType.SEND_MONEY, TransactionType.P2P_SEND -> ErrorRed.copy(alpha = 0.15f)
                                TransactionType.RECEIVE_MONEY, TransactionType.RECEIVE -> SuccessGreen.copy(alpha = 0.15f)
                                TransactionType.BILL_PAYMENT, TransactionType.RECHARGE, TransactionType.PAY_BILLS -> AccentBlue.copy(alpha = 0.15f)
                                else -> TextTertiary.copy(alpha = 0.15f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (transaction.type) {
                            TransactionType.SEND_MONEY, TransactionType.P2P_SEND -> Icons.Default.Send
                            TransactionType.RECEIVE_MONEY, TransactionType.RECEIVE -> Icons.Default.CallReceived
                            else -> Icons.Default.SwapHoriz
                        },
                        contentDescription = transaction.type.name,
                        tint = when (transaction.type) {
                            TransactionType.SEND_MONEY, TransactionType.P2P_SEND -> ErrorRed
                            TransactionType.RECEIVE_MONEY, TransactionType.RECEIVE -> SuccessGreen
                            else -> AccentBlue
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = (transaction.receiverName ?: transaction.senderName ?: transaction.description).ifEmpty { transaction.description },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (transaction.category.isNotEmpty()) {
                            Text(
                                text = transaction.category,
                                fontSize = 10.sp,
                                color = TextTertiary
                            )
                        }
                        Text(
                            text = formatTime(transaction.timestamp.time),
                            fontSize = 10.sp,
                            color = TextTertiary
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${if (transaction.type in listOf(TransactionType.RECEIVE_MONEY, TransactionType.RECEIVE)) "+" else "-"} ₹${String.format("%.2f", transaction.amount)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.type in listOf(TransactionType.RECEIVE_MONEY, TransactionType.RECEIVE)) SuccessGreen else TextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            color = when (transaction.status) {
                                TransactionStatus.SUCCESS, TransactionStatus.COMPLETED -> SuccessGreen.copy(alpha = 0.2f)
                                TransactionStatus.FAILED, TransactionStatus.REJECTED, TransactionStatus.CANCELLED -> ErrorRed.copy(alpha = 0.2f)
                                else -> WarningOrange.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = transaction.status.name,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (transaction.status) {
                            TransactionStatus.SUCCESS, TransactionStatus.COMPLETED -> SuccessGreen
                            TransactionStatus.FAILED, TransactionStatus.REJECTED, TransactionStatus.CANCELLED -> ErrorRed
                            else -> WarningOrange
                        }
                    )
                }
            }
        }
    }
}

private fun getDateKey(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp

    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> "Today"

        calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) &&
            calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) -> "Yesterday"

        else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
}
