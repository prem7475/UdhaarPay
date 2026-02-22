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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.ui.viewmodel.BankAccountViewModel
import com.udhaarpay.app.ui.viewmodel.CreditCardViewModel
import com.udhaarpay.app.ui.viewmodel.DebtViewModel
import com.udhaarpay.app.ui.viewmodel.ExpenseViewModel
import com.udhaarpay.app.ui.viewmodel.NFCTransactionViewModel
import com.udhaarpay.app.ui.viewmodel.UPIPaymentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class StatementRow(
    val title: String,
    val subtitle: String,
    val delta: Double,
    val date: Long
)

@Composable
fun PassbookScreen(
    accountId: Long,
    bankAccountViewModel: BankAccountViewModel = hiltViewModel(),
    upiPaymentViewModel: UPIPaymentViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    debtViewModel: DebtViewModel = hiltViewModel(),
    creditCardViewModel: CreditCardViewModel = hiltViewModel(),
    nfcTransactionViewModel: NFCTransactionViewModel = hiltViewModel()
) {
    val accounts by bankAccountViewModel.accounts.collectAsState()
    val upiPayments by upiPaymentViewModel.payments.collectAsState()
    val expenses by expenseViewModel.expenses.collectAsState()
    val debts by debtViewModel.debts.collectAsState()
    val cards by creditCardViewModel.creditCards.collectAsState()
    val nfcTransactions by nfcTransactionViewModel.transactions.collectAsState()

    val account = accounts.firstOrNull { it.accountId == accountId }
    val accountLabel = account?.bankName ?: "Selected Account"
    val bankNameKey = account?.bankName?.lowercase(Locale.getDefault()).orEmpty()
    val accountNumberKey = account?.accountNumber?.takeLast(4).orEmpty()
    val cardIdsForBank = cards
        .filter { card ->
            bankNameKey.isNotBlank() && card.issuer.lowercase(Locale.getDefault()).contains(bankNameKey.take(4))
        }
        .map { it.cardId }
        .toSet()

    val rows = buildList {
        upiPayments.forEach { payment ->
            add(
                StatementRow(
                    title = if (payment.type == "request") "UPI Request" else "UPI Payment",
                    subtitle = "${payment.senderUPI} -> ${payment.recipientUPI}",
                    delta = if (payment.type == "request") payment.amount else -payment.amount,
                    date = payment.date
                )
            )
        }
        expenses.filter { expense ->
            (expense.accountId != null && expense.accountId == accountId) ||
                (bankNameKey.isNotBlank() && expense.accountName.lowercase(Locale.getDefault()).contains(bankNameKey.take(4))) ||
                (accountNumberKey.isNotBlank() && expense.description.orEmpty().contains(accountNumberKey))
        }.forEach { expense ->
            add(
                StatementRow(
                    title = "${expense.category} Payment",
                    subtitle = expense.description ?: expense.accountName,
                    delta = -expense.amount,
                    date = expense.date
                )
            )
        }
        debts.filter { it.accountId == accountId }.forEach { debt ->
            add(
                StatementRow(
                    title = "Debt Adjustment",
                    subtitle = debt.personName,
                    delta = if (debt.type == "given") -debt.amount else debt.amount,
                    date = debt.date
                )
            )
        }
        nfcTransactions.filter { it.cardId in cardIdsForBank }.forEach { txn ->
            add(
                StatementRow(
                    title = "NFC Payment",
                    subtitle = "${txn.merchant} ••••${txn.cardLast4}",
                    delta = -txn.amount,
                    date = txn.timestamp
                )
            )
        }
    }.sortedBy { it.date }

    var runningBalance = (account?.balance ?: 0.0) - rows.sumOf { it.delta }
    val rowsWithRunning = rows.map { row ->
        runningBalance += row.delta
        row to runningBalance
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Passbook", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            accountLabel,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.padding(6.dp))
        Text(
            "Linked Transactions: ${rowsWithRunning.size}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Spacer(Modifier.padding(6.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(rowsWithRunning) { (row, balanceAfter) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(row.title, fontWeight = FontWeight.SemiBold)
                            Text(row.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(row.date)),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "${if (row.delta < 0) "-" else "+"}INR ${"%.2f".format(kotlin.math.abs(row.delta))}",
                                color = if (row.delta < 0) Color(0xFFEF4444) else Color(0xFF22C55E),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Bal: INR ${"%.2f".format(balanceAfter)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
