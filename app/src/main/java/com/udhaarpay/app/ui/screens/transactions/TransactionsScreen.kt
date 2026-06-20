package com.udhaarpay.app.ui.screens.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.ui.components.PremiumInfoCard
import com.udhaarpay.app.ui.components.PremiumMetricCard
import com.udhaarpay.app.ui.components.PremiumPill
import com.udhaarpay.app.ui.components.PremiumScreen
import com.udhaarpay.app.ui.components.PremiumSectionHeader
import com.udhaarpay.app.ui.viewmodel.DebtViewModel
import com.udhaarpay.app.ui.viewmodel.ExpenseViewModel
import com.udhaarpay.app.ui.viewmodel.NFCTransactionViewModel
import com.udhaarpay.app.ui.viewmodel.TicketViewModel
import com.udhaarpay.app.ui.viewmodel.UPIPaymentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

private data class PassbookEntry(
    val title: String,
    val subtitle: String,
    val amount: Double,
    val date: Long,
    val isDebit: Boolean
)

private enum class TransactionFilter(val title: String) {
    ALL("All"),
    DEBIT("Debit"),
    CREDIT("Credit")
}

@Composable
fun TransactionsScreen(
    upiPaymentViewModel: UPIPaymentViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    ticketViewModel: TicketViewModel = hiltViewModel(),
    nfcTransactionViewModel: NFCTransactionViewModel = hiltViewModel(),
    debtViewModel: DebtViewModel = hiltViewModel()
) {
    val upiPayments by upiPaymentViewModel.payments.collectAsState()
    val expenses by expenseViewModel.expenses.collectAsState()
    val tickets by ticketViewModel.tickets.collectAsState()
    val nfcTransactions by nfcTransactionViewModel.transactions.collectAsState()
    val debts by debtViewModel.debts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(TransactionFilter.ALL) }

    val passbookEntries = buildList {
        upiPayments.forEach { payment ->
            add(
                PassbookEntry(
                    title = if (payment.type == "request") "UPI Request" else "UPI Payment",
                    subtitle = "${payment.senderUPI} -> ${payment.recipientUPI}",
                    amount = payment.amount,
                    date = payment.date,
                    isDebit = payment.type != "request"
                )
            )
        }
        expenses.forEach { expense ->
            add(
                PassbookEntry(
                    title = "Expense | ${expense.category}",
                    subtitle = expense.description ?: expense.accountName,
                    amount = expense.amount,
                    date = expense.date,
                    isDebit = true
                )
            )
        }
        tickets.forEach { ticket ->
            add(
                PassbookEntry(
                    title = "Ticket | ${ticket.ticketType}",
                    subtitle = ticket.provider ?: ticket.destination ?: ticket.movieName ?: "Booking",
                    amount = ticket.amount,
                    date = ticket.date,
                    isDebit = true
                )
            )
        }
        nfcTransactions.forEach { txn ->
            add(
                PassbookEntry(
                    title = "NFC Payment",
                    subtitle = "${txn.merchant} ****${txn.cardLast4}",
                    amount = txn.amount,
                    date = txn.timestamp,
                    isDebit = true
                )
            )
        }
        debts.forEach { debt ->
            add(
                PassbookEntry(
                    title = "Debt | ${debt.category}",
                    subtitle = debt.personName,
                    amount = debt.amount,
                    date = debt.date,
                    isDebit = debt.type.equals("given", true)
                )
            )
        }
    }.sortedByDescending { it.date }

    val totalDebit = remember(passbookEntries) {
        passbookEntries.filter { it.isDebit }.sumOf { it.amount }
    }
    val totalCredit = remember(passbookEntries) {
        passbookEntries.filter { !it.isDebit }.sumOf { it.amount }
    }
    val netFlow = totalCredit - totalDebit
    val normalizedQuery = searchQuery.trim().lowercase(Locale.getDefault())
    val filteredEntries = remember(passbookEntries, selectedFilter, normalizedQuery) {
        passbookEntries.filter { entry ->
            val matchesFilter = when (selectedFilter) {
                TransactionFilter.ALL -> true
                TransactionFilter.DEBIT -> entry.isDebit
                TransactionFilter.CREDIT -> !entry.isDebit
            }
            val matchesSearch = normalizedQuery.isBlank() ||
                entry.title.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                entry.subtitle.lowercase(Locale.getDefault()).contains(normalizedQuery)
            matchesFilter && matchesSearch
        }
    }

    val categorySummary = remember(expenses) {
        expenses.groupBy { it.category.ifBlank { "Miscellaneous" } }
            .mapValues { (_, values) -> values.sumOf { it.amount } }
            .entries.sortedByDescending { it.value }
    }

    PremiumScreen {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                PremiumSectionHeader(
                    title = "Transactions",
                    subtitle = "Passbook, categories, and net cash flow"
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    PremiumMetricCard(
                        title = "Credit",
                        value = "INR ${"%,.0f".format(totalCredit)}",
                        subtitle = "Incoming",
                        modifier = Modifier.weight(1f)
                    )
                    PremiumMetricCard(
                        title = "Debit",
                        value = "INR ${"%,.0f".format(totalDebit)}",
                        subtitle = "Outgoing",
                        modifier = Modifier.weight(1f)
                    )
                    PremiumMetricCard(
                        title = "Net",
                        value = "${if (netFlow >= 0.0) "+" else "-"}INR ${"%,.0f".format(abs(netFlow))}",
                        subtitle = "After dues",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                PremiumInfoCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it.take(40) },
                            singleLine = true,
                            label = { Text("Search by title or counterparty") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TransactionFilter.entries.forEach { filter ->
                                PremiumPill(
                                    text = filter.title,
                                    selected = selectedFilter == filter
                                ) { selectedFilter = filter }
                            }
                        }
                        Text(
                            "${filteredEntries.size} matching transactions",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                PremiumSectionHeader(
                    title = "Category Split",
                    subtitle = "Food, travel, salary, shopping, and more"
                )
            }

            item {
                PremiumInfoCard {
                    if (categorySummary.isEmpty()) {
                        Text("No category data yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        categorySummary.take(6).forEach { (category, amount) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(category, fontWeight = FontWeight.SemiBold)
                                Text("INR ${"%,.0f".format(amount)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item {
                PremiumSectionHeader(
                    title = "Passbook",
                    subtitle = "Latest local transactions"
                )
            }

            if (filteredEntries.isEmpty()) {
                item {
                    PremiumInfoCard {
                        Text(
                            if (passbookEntries.isEmpty()) "No transactions available yet."
                            else "No transactions match your search or filter.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                itemsIndexed(
                    items = filteredEntries,
                    key = { index, entry -> "${entry.date}-${entry.title}-${entry.subtitle}-${index}" }
                ) { _, entry ->
                    PremiumInfoCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.fillMaxWidth(0.72f)) {
                                Text(entry.title, fontWeight = FontWeight.SemiBold)
                                Text(entry.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                Text(
                                    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(entry.date)),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                "${if (entry.isDebit) "-" else "+"}INR ${"%.2f".format(abs(entry.amount))}",
                                color = if (entry.isDebit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
