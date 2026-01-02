package com.example.udhaarpay.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.udhaarpay.ui.components.CommonComponents
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.AnalyticsViewModel
import kotlin.math.roundToInt

@Composable
fun AnalyticsScreen(
    onBack: () -> Unit
) {
    // Mock data for now
    val isLoading by remember { mutableStateOf(false) }
    val error by remember { mutableStateOf<String?>(null) }
    val monthlySpending by remember { mutableStateOf(SpendingAnalytics(
        userId = 1,
        month = "2024-01",
        totalSpent = 15000.0,
        totalReceived = 20000.0,
        weeklyData = "2500,3200,1800,4500,2200,3800,1500",
        monthlyData = "",
        categoryBreakdown = ""
    )) }
    val categoryStats by remember { mutableStateOf(listOf(
        TransactionCategory(userId = 1, categoryName = "Food", amount = 4500.0),
        TransactionCategory(userId = 1, categoryName = "Transport", amount = 3200.0),
        TransactionCategory(userId = 1, categoryName = "Shopping", amount = 2800.0),
        TransactionCategory(userId = 1, categoryName = "Bills", amount = 2500.0),
        TransactionCategory(userId = 1, categoryName = "Entertainment", amount = 2000.0)
    )) }
    val selectedMonth by remember { mutableStateOf("Jan") }

    val errorMessage = error
    var showErrorDialog by remember { mutableStateOf(errorMessage != null) }

    LaunchedEffect(errorMessage) {
        showErrorDialog = errorMessage != null
    }

    if (showErrorDialog && errorMessage != null) {
        CommonComponents.ErrorDialog(
            title = "Error",
            message = errorMessage,
            onDismiss = {
                showErrorDialog = false
            }
        )
    }

    if (isLoading) {
        CommonComponents.LoadingDialog(message = "Loading analytics...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top App Bar
        CommonComponents.PremiumTopAppBar(
            title = "Spending Analytics",
            onBackClick = onBack,
            actions = {
                IconButton(onClick = { viewModel.loadAnalytics() }) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Refresh",
                        tint = NeonOrange
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Total Spending Card
            item {
                TotalSpendingCard(
                    totalSpent = monthlySpending?.totalSpent ?: 0.0,
                    totalReceived = monthlySpending?.totalReceived ?: 0.0
                )
            }

            // Period Selector
            item {
                PeriodSelector(
                    selectedPeriod = selectedMonth,
                    onPeriodSelected = { /* Handle month selection */ }
                )
            }

            // Pie Chart Section
            item {
                PieChartSection(
                    categoryStats = categoryStats,
                    selectedCategory = null
                )
            }

            // Category Breakdown
            item {
                Text(
                    text = "Category Breakdown",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(categoryStats) { stat ->
                CategoryStatCard(
                    categoryName = stat.category,
                    amount = stat.amount,
                    percentage = if (monthlySpending?.totalSpent != null && monthlySpending.totalSpent > 0) {
                        ((stat.amount / monthlySpending.totalSpent) * 100).roundToInt()
                    } else 0,
                    color = getCategoryColor(stat.category)
                )
            }

            // Spending Trends
            item {
                Text(
                    text = "Weekly Trends",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                WeeklyTrendsChart(
                    weeklyData = monthlySpending?.weeklyData ?: ""
                )
            }

            // Insights
            item {
                Text(
                    text = "Insights",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                SpendingInsightsSection(
                    monthlySpending = monthlySpending,
                    topCategories = categoryStats.sortedByDescending { it.amount }.take(3)
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun TotalSpendingCard(
    totalSpent: Double,
    totalReceived: Double
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Total Spending",
                fontSize = 14.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "₹${totalSpent.toLong()}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = NeonOrange
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Spent",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${totalSpent.toLong()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Received",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${totalReceived.toLong()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    val periods = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(periods) { month ->
            FilterChip(
                selected = selectedPeriod == month,
                onClick = { onPeriodSelected(month) },
                label = {
                    Text(
                        text = month,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                modifier = Modifier.height(36.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonOrange,
                    selectedLabelColor = DarkBackground,
                    containerColor = DarkCard,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    selectedBorderColor = NeonOrange,
                    borderColor = DarkCard
                )
            )
        }
    }
}

@Composable
private fun PieChartSection(
    categoryStats: List<com.example.udhaarpay.data.model.TransactionCategory>,
    selectedCategory: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (categoryStats.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "No data",
                    tint = TextSecondary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No spending data available",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            SimplePieChart(
                categoryStats = categoryStats,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun SimplePieChart(
    categoryStats: List<com.example.udhaarpay.data.model.TransactionCategory>,
    modifier: Modifier = Modifier
) {
    val total = categoryStats.sumOf { it.amount }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Simple donut chart visualization
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(50))
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${categoryStats.size}\nCategories",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NeonOrange,
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categoryStats.take(3)) { stat ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkBackground)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(getCategoryColor(stat.category))
                    )
                    Text(
                        text = "${stat.category}: ₹${stat.amount.toLong()}",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryStatCard(
    categoryName: String,
    amount: Double,
    percentage: Int,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(color)
                    )
                    Text(
                        text = categoryName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "$percentage%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(DarkBackground)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = percentage / 100f)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "₹${amount.toLong()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun WeeklyTrendsChart(
    weeklyData: String
) {
    val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val mockData = listOf(2500.0, 3200.0, 1800.0, 4500.0, 2200.0, 3800.0, 1500.0)
    val maxAmount = mockData.maxOrNull() ?: 5000.0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            repeat(7) { index ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Bar
                    val barHeight = (mockData[index] / maxAmount * 130).dp
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(NeonOrange)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Label
                    Text(
                        text = weekDays[index],
                        fontSize = 10.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SpendingInsightsSection(
    monthlySpending: com.example.udhaarpay.data.model.SpendingAnalytics?,
    topCategories: List<com.example.udhaarpay.data.model.TransactionCategory>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InsightItem(
            title = "Highest Spending",
            value = topCategories.firstOrNull()?.category ?: "N/A",
            amount = "₹${topCategories.firstOrNull()?.amount?.toLong() ?: 0}",
            backgroundColor = CardGradient1Start
        )

        InsightItem(
            title = "Average Daily Spend",
            value = "Last 30 days",
            amount = "₹${(monthlySpending?.totalSpent ?: 0.0 / 30).toLong()}",
            backgroundColor = CardGradient2Start
        )

        InsightItem(
            title = "Total Balance",
            value = "Across all accounts",
            amount = "₹${monthlySpending?.totalReceived?.toLong() ?: 0}",
            backgroundColor = CardGradient3Start
        )
    }
}

@Composable
private fun InsightItem(
    title: String,
    value: String,
    amount: String,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor.copy(alpha = 0.2f))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = amount,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = backgroundColor
            )
        }
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category) {
        "Food" -> Color(0xFFFF9800)
        "Transport" -> Color(0xFF2196F3)
        "Health" -> Color(0xFF4CAF50)
        "Shopping" -> Color(0xFFE91E63)
        "Bills" -> Color(0xFF9C27B0)
        "Travel" -> Color(0xFF00BCD4)
        "Entertainment" -> Color(0xFFFFC107)
        else -> AccentBlue
    }
}
