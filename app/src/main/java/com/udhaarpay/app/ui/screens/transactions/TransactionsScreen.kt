package com.udhaarpay.app.ui.screens.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.ui.viewmodel.DebtViewModel
import com.udhaarpay.app.ui.viewmodel.NFCTransactionViewModel
import com.udhaarpay.app.ui.viewmodel.ExpenseViewModel
import com.udhaarpay.app.ui.viewmodel.TicketViewModel
import com.udhaarpay.app.ui.viewmodel.UPIPaymentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class PassbookEntry(
    val title: String,
    val subtitle: String,
    val amount: Double,
    val date: Long,
    val isDebit: Boolean
)

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
                    subtitle = "${txn.merchant} ••••${txn.cardLast4}",
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
        Spacer(modifier = Modifier.padding(6.dp))

        if (passbookEntries.isEmpty()) {
            Text("No transactions available yet.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(passbookEntries) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
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
                                "${if (entry.isDebit) "-" else "+"}INR ${"%.2f".format(entry.amount)}",
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
