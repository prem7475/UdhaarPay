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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Passbook & Transactions", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text(
            "All local-device transactions in one view",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryStat(
                title = "Credit",
                value = "INR ${"%.2f".format(totalCredit)}",
                modifier = Modifier.weight(1f)
            )
            SummaryStat(
                title = "Debit",
                value = "INR ${"%.2f".format(totalDebit)}",
                modifier = Modifier.weight(1f)
            )
            SummaryStat(
                title = "Net",
                value = "${if (netFlow >= 0.0) "+" else "-"}INR ${"%.2f".format(abs(netFlow))}",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it.take(40) },
            singleLine = true,
            label = { Text("Search by title or counterparty") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TransactionFilter.entries.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter.title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "${filteredEntries.size} transactions",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (filteredEntries.isEmpty()) {
            Text(
                if (passbookEntries.isEmpty()) "No transactions available yet."
                else "No transactions match your search/filter.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = filteredEntries,
                    key = { index, entry -> "${entry.date}-${entry.title}-${entry.subtitle}-${index}" }
                ) { _, entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    entry.title,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    entry.subtitle,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                                Text(
                                    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                        .format(Date(entry.date)),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
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

@Composable
private fun SummaryStat(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}
