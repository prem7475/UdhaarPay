package com.udhaarpay.app.ui.screens.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.local.entities.Expense
import com.udhaarpay.app.ui.viewmodel.BankAccountViewModel
import com.udhaarpay.app.ui.viewmodel.CreditCardViewModel
import com.udhaarpay.app.ui.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel = hiltViewModel(),
    bankAccountViewModel: BankAccountViewModel = hiltViewModel(),
    creditCardViewModel: CreditCardViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val bankAccounts by bankAccountViewModel.accounts.collectAsState()
    val cards by creditCardViewModel.creditCards.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val sortedExpenses = remember(expenses) { expenses.sortedByDescending { it.date } }

    val monthlyTotal = expenses
        .filter { SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(it.date)) == currentMonth() }
        .sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Expense Tracking", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text("Month: ${currentMonth()}")
                Text("Total spent: INR ${"%.2f".format(monthlyTotal)}", fontWeight = FontWeight.SemiBold)
                Text("Entries: ${expenses.size}")
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = { showAddDialog = true }) {
            Text("Add Expense")
        }
        Spacer(Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = sortedExpenses, key = { it.expenseId }) { expense ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(expense.category, fontWeight = FontWeight.SemiBold)
                            Text(expense.description ?: "-", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Paid via ${expense.accountName}", fontSize = 12.sp)
                            Text(
                                SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(expense.date)),
                                fontSize = 12.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("INR ${expense.amount}", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                            TextButton(onClick = { viewModel.delete(expense) }) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            bankAccounts = bankAccounts.map { Triple("bank", it.bankName, it.accountId) } +
                cards.map { Triple("card", "${it.issuer} ••••${it.cardNumber.takeLast(4)}", it.cardId) } +
                listOf(Triple("wallet", "Cash Wallet", null)),
            onDismiss = { showAddDialog = false },
            onSave = { amount, category, accountType, accountName, accountId, description ->
                viewModel.insert(
                    Expense(
                        amount = amount,
                        category = category,
                        subCategory = null,
                        account = accountType,
                        accountName = accountName,
                        description = description,
                        date = System.currentTimeMillis(),
                        month = currentMonth(),
                        receiptUrl = null,
                        accountId = accountId
                    )
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddExpenseDialog(
    bankAccounts: List<Triple<String, String, Long?>>,
    onDismiss: () -> Unit,
    onSave: (Double, String, String, String, Long?, String?) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food") }
    var accountType by remember { mutableStateOf(bankAccounts.firstOrNull()?.first ?: "wallet") }
    var accountName by remember { mutableStateOf(bankAccounts.firstOrNull()?.second ?: "Cash Wallet") }
    var accountId by remember { mutableStateOf(bankAccounts.firstOrNull()?.third) }
    var description by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Amount") }
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (Food/Travel/Salary/Shopping/Other)") }
                )
                OutlinedTextField(
                    value = accountName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Account") }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    bankAccounts.take(4).forEach { option ->
                        TextButton(onClick = {
                            accountType = option.first
                            accountName = option.second
                            accountId = option.third
                        }) {
                            Text(option.second.take(10))
                        }
                    }
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") }
                )
                if (!error.isNullOrBlank()) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amountValue = amount.toDoubleOrNull()
                when {
                    amountValue == null || amountValue <= 0.0 -> error = "Enter valid amount"
                    category.isBlank() -> error = "Category is required"
                    accountName.isBlank() -> error = "Account is required"
                    else -> onSave(
                        amountValue,
                        category.trim(),
                        accountType,
                        accountName.trim(),
                        accountId,
                        description.ifBlank { null }
                    )
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private fun currentMonth(): String {
    return SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date())
}
