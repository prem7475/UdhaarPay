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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.data.model.Transaction
import com.example.udhaarpay.ui.components.PremiumTopAppBar
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    onBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    
    // Filters
    var selectedCategory by remember { mutableStateOf("All") }
    var sourceFilter by remember { mutableStateOf(SourceFilter.ALL) }
    
    // Date Filter State
    var showDatePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()
    val isDateFilterActive = dateRangePickerState.selectedStartDateMillis != null

    val categories = listOf("All", "Food", "Transport", "Health", "Shopping", "Bills", "Travel", "Entertainment", "Other")
    
    val filteredTransactions = remember(transactions, selectedCategory, sourceFilter, dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis) {
        transactions.filter { transaction ->
            // Category Filter
            val matchCategory = selectedCategory == "All" || transaction.category.equals(selectedCategory, ignoreCase = true)
            
            // Source Filter
            val matchSource = when (sourceFilter) {
                SourceFilter.ALL -> true
                SourceFilter.WALLET -> transaction.paymentMethod.equals("WALLET", ignoreCase = true)
                SourceFilter.BANK -> !transaction.paymentMethod.equals("WALLET", ignoreCase = true)
            }
            
            // Date Filter
            val matchDate = if (dateRangePickerState.selectedStartDateMillis != null) {
                val txTime = transaction.timestamp // It's already Long
                val start = dateRangePickerState.selectedStartDateMillis!!
                val end = dateRangePickerState.selectedEndDateMillis ?: start
                val actualEnd = end + 86400000 - 1 // End of the day
                txTime in start..actualEnd
            } else {
                true
            }

            matchCategory && matchSource && matchDate
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Apply", color = NeonOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    // Clear date filter
                    dateRangePickerState.setSelection(null, null)
                    showDatePicker = false 
                }) {
                    Text("Clear", color = TextSecondary)
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f)
            )
        }
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

        // Filters Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            // Row 1: Date & Source
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Date Filter Chip
                FilterChip(
                    selected = isDateFilterActive,
                    onClick = { showDatePicker = true },
                    label = { 
                        Text(
                            if (isDateFilterActive) "Date: Custom" else "Date: All Time",
                            fontSize = 12.sp
                        ) 
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonOrange.copy(alpha = 0.2f),
                        selectedLabelColor = NeonOrange,
                        selectedLeadingIconColor = NeonOrange,
                        containerColor = DarkCard,
                        labelColor = TextSecondary,
                        leadingIconColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isDateFilterActive) NeonOrange else TextTertiary.copy(alpha = 0.3f),
                        selectedBorderColor = NeonOrange,
                        enabled = true,
                        selected = isDateFilterActive
                    )
                )

                // Source Filter Chip
                Box {
                    var showSourceMenu by remember { mutableStateOf(false) }
                    FilterChip(
                        selected = sourceFilter != SourceFilter.ALL,
                        onClick = { showSourceMenu = true },
                        label = { 
                            Text(
                                "Source: ${sourceFilter.label}",
                                fontSize = 12.sp
                            ) 
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (sourceFilter == SourceFilter.WALLET) Icons.Default.AccountBalanceWallet else Icons.Default.AccountBalance,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonOrange.copy(alpha = 0.2f),
                            selectedLabelColor = NeonOrange,
                            selectedLeadingIconColor = NeonOrange,
                            containerColor = DarkCard,
                            labelColor = TextSecondary,
                            leadingIconColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (sourceFilter != SourceFilter.ALL) NeonOrange else TextTertiary.copy(alpha = 0.3f),
                            selectedBorderColor = NeonOrange,
                            enabled = true,
                            selected = sourceFilter != SourceFilter.ALL
                        )
                    )
                    
                    DropdownMenu(
                        expanded = showSourceMenu,
                        onDismissRequest = { showSourceMenu = false },
                        modifier = Modifier.background(DarkCard)
                    ) {
                        SourceFilter.values().forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter.label, color = TextPrimary) },
                                onClick = {
                                    sourceFilter = filter
                                    showSourceMenu = false
                                },
                                leadingIcon = {
                                    if (sourceFilter == filter) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = NeonOrange)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Categories
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        border = if (selectedCategory == category) null else FilterChipDefaults.filterChipBorder(
                            borderColor = TextTertiary.copy(alpha = 0.3f),
                            enabled = true,
                            selected = false
                        )
                    )
                }
            }
        }

        if (isLoading && filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonOrange)
            }
        } else if (filteredTransactions.isEmpty()) {
            EmptyState(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredTransactions.groupBy { getDateKey(it.timestamp) }.toList()) { (dateKey, dayTransactions) ->
                    TransactionGroup(dateKey = dateKey, transactions = dayTransactions)
                }
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(DarkCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = "No transactions",
                    tint = TextTertiary.copy(alpha = 0.5f),
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Transactions Found",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try adjusting your filters to see more results.",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
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
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TransactionItemCard(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                // Icon Logic
                val isCredit = !transaction.isDebit
                val isWallet = transaction.paymentMethod.equals("WALLET", ignoreCase = true)
                
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            color = when {
                                isCredit -> SuccessGreen.copy(alpha = 0.1f)
                                isWallet -> AccentBlue.copy(alpha = 0.1f)
                                else -> ErrorRed.copy(alpha = 0.1f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isCredit -> Icons.Default.ArrowDownward
                            isWallet -> Icons.Default.AccountBalanceWallet
                            else -> Icons.Default.ArrowUpward
                        },
                        contentDescription = transaction.type,
                        tint = when {
                            isCredit -> SuccessGreen
                            isWallet -> AccentBlue
                            else -> ErrorRed
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = (transaction.receiverName ?: transaction.senderName ?: transaction.description).ifEmpty { transaction.description },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = transaction.category.capitalize(),
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Box(modifier = Modifier.size(3.dp).background(TextTertiary, CircleShape))
                        Text(
                            text = formatTime(transaction.timestamp),
                            fontSize = 11.sp,
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
                    text = "${if (transaction.isDebit) "-" else "+"} ₹${String.format("%.2f", transaction.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.isDebit) TextPrimary else SuccessGreen
                )
                
                // Status Badge
                val isSuccess = transaction.status.equals("SUCCESS", ignoreCase = true) || 
                                transaction.status.equals("COMPLETED", ignoreCase = true)
                
                if (!isSuccess) {
                    val isFailed = transaction.status.equals("FAILED", ignoreCase = true) || 
                                   transaction.status.equals("REJECTED", ignoreCase = true) || 
                                   transaction.status.equals("CANCELLED", ignoreCase = true)
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                color = if (isFailed) ErrorRed.copy(alpha = 0.2f) else WarningOrange.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = transaction.status.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isFailed) ErrorRed else WarningOrange
                        )
                    }
                } else {
                     Text(
                        text = if (transaction.paymentMethod.equals("WALLET", ignoreCase = true)) "Wallet" else "Bank",
                        fontSize = 10.sp,
                        color = TextTertiary
                    )
                }
            }
        }
    }
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
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
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
}

enum class SourceFilter(val label: String) {
    ALL("All"),
    WALLET("Wallet"),
    BANK("Bank")
}
