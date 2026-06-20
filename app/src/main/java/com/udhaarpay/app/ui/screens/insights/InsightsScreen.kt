package com.udhaarpay.app.ui.screens.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.local.entities.CreditCard
import com.udhaarpay.app.data.local.entities.Expense
import com.udhaarpay.app.ui.components.PremiumInfoCard
import com.udhaarpay.app.ui.components.PremiumMetricCard
import com.udhaarpay.app.ui.components.PremiumPill
import com.udhaarpay.app.ui.components.PremiumScreen
import com.udhaarpay.app.ui.components.PremiumSectionHeader
import com.udhaarpay.app.ui.theme.GlassTint
import com.udhaarpay.app.ui.theme.LossRed
import com.udhaarpay.app.ui.theme.ProfitGreen
import com.udhaarpay.app.ui.viewmodel.BankAccountViewModel
import com.udhaarpay.app.ui.viewmodel.CreditCardViewModel
import com.udhaarpay.app.ui.viewmodel.DebtViewModel
import com.udhaarpay.app.ui.viewmodel.PaperTradingViewModel
import com.udhaarpay.app.ui.viewmodel.UPIPaymentViewModel
import com.udhaarpay.app.ui.viewmodel.WalletViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

private enum class SortMode(val label: String) {
    Recent("Recent"),
    Amount("Amount"),
    Category("Category"),
    Source("Source")
}

@Composable
fun InsightsScreen(
    debtViewModel: DebtViewModel = hiltViewModel(),
    bankAccountViewModel: BankAccountViewModel = hiltViewModel(),
    creditCardViewModel: CreditCardViewModel = hiltViewModel(),
    upiPaymentViewModel: UPIPaymentViewModel = hiltViewModel(),
    walletViewModel: WalletViewModel = hiltViewModel(),
    paperTradingViewModel: PaperTradingViewModel = hiltViewModel()
) {
    val expenses by debtViewModel.expenses.collectAsState()
    val debts by debtViewModel.debts.collectAsState()
    val bankAccounts by bankAccountViewModel.accounts.collectAsState()
    val cards by creditCardViewModel.creditCards.collectAsState()
    val payments by upiPaymentViewModel.payments.collectAsState()
    val wallet by walletViewModel.walletAccount.collectAsState()
    val paperAccount by paperTradingViewModel.account.collectAsState()
    val paperSummary by paperTradingViewModel.portfolioSummary.collectAsState()
    val totalGiven by debtViewModel.totalGiven.collectAsState()
    val totalTaken by debtViewModel.totalTaken.collectAsState()
    val totalExpense by debtViewModel.totalExpense.collectAsState()
    val totalIncome by debtViewModel.totalIncome.collectAsState()
    val spendingByCategory by debtViewModel.spendingByCategory.collectAsState()

    var sortMode by remember { mutableStateOf(SortMode.Recent) }

    val primaryAccounts = remember(bankAccounts, wallet) {
        bankAccounts.filter { !it.accountType.equals("Wallet", true) && !it.accountType.equals("Cash", true) }
    }
    val bankBalance = primaryAccounts.sumOf { it.balance }
    val walletBalance = wallet?.balance ?: bankAccounts.firstOrNull { it.accountType.equals("Wallet", true) }?.balance ?: 0.0
    val cardOutstanding = cards.sumOf { (it.limit - it.balance).coerceAtLeast(0.0) }
    val availableCash = bankBalance + walletBalance + (paperAccount?.virtualBalance ?: 0.0)
    val netAfterClearing = availableCash + totalGiven - totalTaken - cardOutstanding

    val sortedExpenses = remember(expenses, sortMode) {
        when (sortMode) {
            SortMode.Recent -> expenses.sortedByDescending { it.date }
            SortMode.Amount -> expenses.sortedByDescending { it.amount }
            SortMode.Category -> expenses.sortedWith(compareBy<Expense> { it.category.lowercase(Locale.getDefault()) }.thenByDescending { it.date })
            SortMode.Source -> expenses.sortedWith(compareBy<Expense> { it.account.lowercase(Locale.getDefault()) }.thenByDescending { it.date })
        }
    }

    val topCategories = remember(spendingByCategory) {
        spendingByCategory.entries.sortedByDescending { it.value }.take(6)
    }

    PremiumScreen {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PremiumSectionHeader(
                    title = "Spending Insights",
                    subtitle = "Track spend, dues, balances, and what is left after clearing liabilities."
                )
            }

            item {
                SummaryGrid(
                    items = listOf(
                        "Bank" to bankBalance,
                        "Wallet" to walletBalance,
                        "Cards Due" to cardOutstanding,
                        "Expenses" to totalExpense,
                        "Income" to totalIncome,
                        "Net After Dues" to netAfterClearing
                    )
                )
            }

            item {
                PremiumInfoCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Category Split", fontWeight = FontWeight.SemiBold)
                        if (topCategories.isEmpty()) {
                            Text("No spending data yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            CategoryChart(entries = topCategories)
                        }
                    }
                }
            }

            item {
                PremiumInfoCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Sort Payments", fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            SortMode.entries.take(2).forEach { mode ->
                                PremiumPill(
                                    text = mode.label,
                                    selected = sortMode == mode,
                                    modifier = Modifier.weight(1f)
                                ) { sortMode = mode }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            SortMode.entries.drop(2).forEach { mode ->
                                PremiumPill(
                                    text = mode.label,
                                    selected = sortMode == mode,
                                    modifier = Modifier.weight(1f)
                                ) { sortMode = mode }
                            }
                        }

                        if (sortedExpenses.isEmpty()) {
                            Text("No payment history yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            sortedExpenses.take(10).forEach { expense ->
                                ExpenseRow(expense = expense)
                            }
                        }
                    }
                }
            }

            item {
                PremiumInfoCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Debt, Lending, and Dues", fontWeight = FontWeight.SemiBold)
                        MetricLine("Money I gave / to receive", totalGiven, ProfitGreen)
                        MetricLine("Money I owe", totalTaken, LossRed)
                        MetricLine("Card minimum dues", cards.sumOf { it.minimumDue }, Color(0xFFF59E0B))
                        MetricLine("Paper trading balance", paperSummary.totalPortfolioValue, MaterialTheme.colorScheme.secondary)
                        Text(
                            "Outstanding after clearing dues: INR ${"%.2f".format(netAfterClearing)}",
                            fontWeight = FontWeight.Bold,
                            color = if (netAfterClearing >= 0) ProfitGreen else LossRed
                        )
                    }
                }
            }

            item {
                PremiumInfoCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Card Due Dates", fontWeight = FontWeight.SemiBold)
                        if (cards.isEmpty()) {
                            Text("No cards added yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            cards.forEach { card ->
                                CardDueRow(card = card)
                            }
                        }
                    }
                }
            }

            item {
                PremiumInfoCard {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Payment Sources", fontWeight = FontWeight.SemiBold)
                        Text("Bank accounts: ${primaryAccounts.size}")
                        Text("Wallet balance: INR ${"%.2f".format(walletBalance)}")
                        Text("Paper trading: INR ${"%.2f".format(paperSummary.totalPortfolioValue)}")
                        Text("UPI payments recorded: ${payments.size}")
                        Text("Debt entries: ${debts.size}")
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryGrid(items: List<Pair<String, Double>>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { (label, amount) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = GlassTint),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            Text(
                                "INR ${"%,.2f".format(amount)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryChart(entries: List<Map.Entry<String, Double>>) {
    val maxValue = max(1.0, entries.maxOfOrNull { it.value } ?: 1.0)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        entries.forEachIndexed { index, entry ->
            val fillRatio = (entry.value / maxValue).coerceIn(0.08, 1.0)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(entry.key, fontSize = 12.sp)
                    Text("INR ${"%.0f".format(entry.value)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(999.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fillRatio.toFloat())
                            .height(12.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        if (index % 2 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                        Color(0xFFFEA8A8)
                                    )
                                ),
                                RoundedCornerShape(999.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseRow(expense: Expense) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        if (expense.amount >= 0) MaterialTheme.colorScheme.primary else LossRed,
                        RoundedCornerShape(50)
                    )
            )
            Spacer(modifier = Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.category.ifBlank { "Miscellaneous" }, fontWeight = FontWeight.SemiBold)
                Text(
                    expense.description ?: expense.subCategory ?: expense.accountName,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Text(
                    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(expense.date)),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
            Text(
                "-INR ${"%.2f".format(expense.amount)}",
                fontWeight = FontWeight.Bold,
                color = LossRed
            )
        }
    }
}

@Composable
private fun MetricLine(label: String, amount: Double, tint: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Text("INR ${"%.2f".format(amount)}", fontWeight = FontWeight.SemiBold, color = tint)
    }
}

@Composable
private fun CardDueRow(card: CreditCard) {
    val dueText = remember(card.billDueDateMillis) {
        card.billDueDateMillis?.let {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
        } ?: "Not scheduled"
    }
    val outstanding = (card.limit - card.balance).coerceAtLeast(0.0)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("${card.issuer} ${card.cardType}", fontWeight = FontWeight.SemiBold)
            Text("Due: $dueText", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("INR ${"%.2f".format(outstanding)}", fontWeight = FontWeight.Bold)
            Text("Min ${"%.2f".format(card.minimumDue)}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}
