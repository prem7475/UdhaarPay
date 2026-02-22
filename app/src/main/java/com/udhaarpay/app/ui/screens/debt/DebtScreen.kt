package com.udhaarpay.app.ui.screens.debt

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.udhaarpay.app.data.local.entities.BankAccount
import com.udhaarpay.app.data.local.entities.Debt
import com.udhaarpay.app.data.local.entities.Expense
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.components.UdhaarPayTextButton
import com.udhaarpay.app.ui.viewmodel.CreditCardViewModel
import com.udhaarpay.app.ui.viewmodel.DebtViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DebtScreen(
    viewModel: DebtViewModel = hiltViewModel(),
    creditCardViewModel: CreditCardViewModel = hiltViewModel()
) {
    val debts by viewModel.debts.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val accounts by viewModel.linkedAccounts.collectAsState()
    val totalGiven by viewModel.totalGiven.collectAsState()
    val totalTaken by viewModel.totalTaken.collectAsState()
    val netPosition by viewModel.netPosition.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val spendingByCategory by viewModel.spendingByCategory.collectAsState()
    val status by viewModel.statusMessage.collectAsState()
    val cards by creditCardViewModel.creditCards.collectAsState()

    val animatedGiven by animateFloatAsState(targetValue = totalGiven.toFloat(), label = "given")
    val animatedTaken by animateFloatAsState(targetValue = totalTaken.toFloat(), label = "taken")
    val animatedNet by animateFloatAsState(targetValue = netPosition.toFloat(), label = "net")
    val animatedExpense by animateFloatAsState(targetValue = totalExpense.toFloat(), label = "expense")
    val animatedIncome by animateFloatAsState(targetValue = totalIncome.toFloat(), label = "income")

    var showAddDialog by remember { mutableStateOf(false) }
    var showRecordDialog by remember { mutableStateOf(false) }
    var settleDialogDebt by remember { mutableStateOf<Debt?>(null) }
    var editAmountAccount by remember { mutableStateOf<BankAccount?>(null) }

    val orderedDebts = debts.sortedByDescending { it.date }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Debt & Spend Manager", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        SummaryCard(
            totalGiven = animatedGiven.toDouble(),
            totalTaken = animatedTaken.toDouble(),
            net = animatedNet.toDouble(),
            expense = animatedExpense.toDouble(),
            income = animatedIncome.toDouble()
        )

        Text("Accounts / Wallet / Cash", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        AccountBalancesSection(
            accounts = accounts,
            onEditAmount = { editAmountAccount = it }
        )

        SpendingAnalysisSection(spendingByCategory = spendingByCategory)

        Text("Recent Spend Records", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        RecentExpensesSection(expenses = expenses)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            UdhaarPayButton(text = "Add Debt Entry", onClick = { showAddDialog = true }, modifier = Modifier.weight(1f))
            UdhaarPayButton(text = "Add Spend/Income", onClick = { showRecordDialog = true }, modifier = Modifier.weight(1f))
        }
        UdhaarPayButton(
            text = "Track Settlements",
            onClick = { settleDialogDebt = orderedDebts.firstOrNull() },
            modifier = Modifier.fillMaxWidth()
        )

        if (!status.isNullOrBlank()) {
            Text(status ?: "", color = MaterialTheme.colorScheme.primary)
            UdhaarPayTextButton(text = "Dismiss", onClick = { viewModel.clearStatus() })
        }

        Text("Debt Entries", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (orderedDebts.isEmpty()) {
            Text("No debt entries yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            orderedDebts.forEach { debt ->
                DebtEntryCard(
                    debt = debt,
                    onSettle = { settleDialogDebt = debt },
                    onDelete = { viewModel.delete(debt) }
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    if (showAddDialog) {
        AddDebtDialog(
            bankAccounts = accounts.map { it.accountId to "${it.bankName} ${it.accountNumber}" },
            cards = cards.map { it.cardId to "${it.issuer} ${it.cardNumber}" },
            onDismiss = { showAddDialog = false },
            onSave = { person, amount, type, category, source, accountId, reason ->
                viewModel.addDebtEntry(
                    personName = person,
                    amount = amount,
                    type = type,
                    category = category,
                    paymentSource = source,
                    accountId = accountId,
                    reason = reason
                )
                showAddDialog = false
            }
        )
    }

    if (showRecordDialog) {
        AddSpendIncomeDialog(
            bankAccounts = accounts.map { it.accountId to "${it.bankName} ${it.accountNumber}" },
            cards = cards.map { it.cardId to "${it.issuer} ${it.cardNumber}" },
            onDismiss = { showRecordDialog = false },
            onSave = { amount, isIncome, category, source, accountId, description ->
                viewModel.addSpendOrIncomeEntry(
                    amount = amount,
                    isIncome = isIncome,
                    category = category,
                    source = source,
                    accountId = accountId,
                    description = description
                )
                showRecordDialog = false
            }
        )
    }

    settleDialogDebt?.let { debt ->
        SettleDebtDialog(
            debt = debt,
            bankAccounts = accounts.map { it.accountId to "${it.bankName} ${it.accountNumber}" },
            cards = cards.map { it.cardId to "${it.issuer} ${it.cardNumber}" },
            onDismiss = { settleDialogDebt = null },
            onSettle = { amount, source, accountId ->
                viewModel.settleDebtPartial(
                    debt = debt,
                    settledAmount = amount,
                    settlementSource = source,
                    settlementAccountId = accountId
                )
                settleDialogDebt = null
            }
        )
    }

    editAmountAccount?.let { account ->
        EditAccountAmountDialog(
            account = account,
            onDismiss = { editAmountAccount = null },
            onSave = { amount ->
                viewModel.updateAccountAmount(account.accountId, amount)
                editAmountAccount = null
            }
        )
    }
}

@Composable
private fun DebtEntryCard(
    debt: Debt,
    onSettle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${if (debt.type == "given") "Given" else "Taken"} | ${debt.category}",
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                Text(
                    debt.status.replaceFirstChar { it.uppercase() },
                    color = if (debt.status == "settled") Color(0xFF22C55E) else Color(0xFFF59E0B)
                )
            }
            Text("Person: ${debt.personName}")
            Text("Amount: INR ${debt.amount}")
            Text(
                "Settled: INR ${"%.2f".format(debt.amountSettled ?: 0.0)} | Source: ${debt.paymentSource}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            Text(
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(debt.date)),
                fontSize = 12.sp
            )
            if (!debt.reason.isNullOrBlank()) {
                Text(debt.reason.orEmpty(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onSettle) { Text("Settle / Partial") }
                TextButton(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}

@Composable
private fun SummaryCard(totalGiven: Double, totalTaken: Double, net: Double, expense: Double, income: Double) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text("Summary", fontWeight = FontWeight.SemiBold)
            Text("Expense So Far: INR ${"%.2f".format(expense)}", color = Color(0xFFEF4444))
            Text("Income So Far: INR ${"%.2f".format(income)}", color = Color(0xFF22C55E))
            Text("Total Given: INR ${"%.2f".format(totalGiven)}")
            Text("Total Taken: INR ${"%.2f".format(totalTaken)}")
            Text(
                "Net Position: INR ${"%.2f".format(net)}",
                color = if (net >= 0) Color(0xFF22C55E) else Color(0xFFEF4444),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AccountBalancesSection(
    accounts: List<BankAccount>,
    onEditAmount: (BankAccount) -> Unit
) {
    if (accounts.isEmpty()) {
        Text("No accounts found.")
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        accounts.forEach { account ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${account.bankName} (${account.accountType})", fontWeight = FontWeight.SemiBold)
                        Text("Balance: INR ${"%.2f".format(account.balance)}")
                    }
                    TextButton(onClick = { onEditAmount(account) }) {
                        Text("Set Amount")
                    }
                }
            }
        }
    }
}

@Composable
private fun SpendingAnalysisSection(spendingByCategory: Map<String, Double>) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text("Spending Analysis", fontWeight = FontWeight.SemiBold)
            if (spendingByCategory.isEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text("No spending records yet.")
                return@Column
            }

            val max = spendingByCategory.values.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0
            spendingByCategory.entries.take(8).forEach { (category, amount) ->
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(category, modifier = Modifier.width(92.dp), fontSize = 12.sp)
                    val normalizedWidth = (180 * (amount / max).coerceIn(0.0, 1.0)).dp
                    Card(
                        modifier = Modifier
                            .height(10.dp)
                            .width(normalizedWidth),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD71926))
                    ) {}
                    Spacer(Modifier.width(8.dp))
                    Text("INR ${"%.0f".format(amount)}", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun RecentExpensesSection(expenses: List<Expense>) {
    if (expenses.isEmpty()) {
        Text("No spending history yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        expenses.sortedByDescending { it.date }.take(12).forEach { expense ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(expense.category.ifBlank { "Miscellaneous" }, fontWeight = FontWeight.SemiBold)
                        Text(
                            expense.description ?: "Miscellaneous",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault()).format(Date(expense.date)),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("-INR ${"%.2f".format(expense.amount)}", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EditAccountAmountDialog(
    account: BankAccount,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf(account.balance.toString()) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Amount") },
        text = {
            Column {
                Text("${account.bankName} (${account.accountType})")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (!error.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amount = amountText.toDoubleOrNull()
                if (amount == null || amount < 0.0) {
                    error = "Enter valid amount"
                } else {
                    onSave(amount)
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun AddSpendIncomeDialog(
    bankAccounts: List<Pair<Long, String>>,
    cards: List<Pair<Long, String>>,
    onDismiss: () -> Unit,
    onSave: (Double, Boolean, String, String, Long?, String?) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("expense") }
    var category by remember { mutableStateOf("Miscellaneous") }
    var source by remember { mutableStateOf("Bank") }
    var accountId by remember { mutableStateOf<Long?>(bankAccounts.firstOrNull()?.first) }
    var description by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val sourceAccounts = if (source.equals("Card", true)) cards else bankAccounts

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Spend / Income") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = mode,
                    onValueChange = { mode = it.lowercase(Locale.getDefault()) },
                    label = { Text("Mode (expense/income)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = source,
                    onValueChange = {
                        source = it
                        accountId = if (it.equals("Card", true)) cards.firstOrNull()?.first else bankAccounts.firstOrNull()?.first
                    },
                    label = { Text("Source (Bank/Card/Wallet/Cash)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    sourceAccounts.take(4).forEach { account ->
                        TextButton(onClick = { accountId = account.first }) {
                            Text(account.second.take(10))
                        }
                    }
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (!error.isNullOrBlank()) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amount = amountText.toDoubleOrNull()
                when {
                    amount == null || amount <= 0.0 -> error = "Enter valid amount"
                    mode !in setOf("expense", "income") -> error = "Mode must be expense or income"
                    else -> onSave(
                        amount,
                        mode == "income",
                        category.ifBlank { "Miscellaneous" },
                        source,
                        accountId,
                        description.ifBlank { null }
                    )
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun AddDebtDialog(
    bankAccounts: List<Pair<Long, String>>,
    cards: List<Pair<Long, String>>,
    onDismiss: () -> Unit,
    onSave: (String, Double, String, String, String, Long?, String?) -> Unit
) {
    var person by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("given") }
    var category by remember { mutableStateOf("Miscellaneous") }
    var source by remember { mutableStateOf("Bank") }
    var accountId by remember { mutableStateOf<Long?>(bankAccounts.firstOrNull()?.first) }
    var reason by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Debt Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = person, onValueChange = { person = it }, label = { Text("Person Name") })
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Amount") }
                )
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it.lowercase(Locale.getDefault()) },
                    label = { Text("Type (given/taken)") }
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") }
                )
                OutlinedTextField(
                    value = source,
                    onValueChange = {
                        source = it
                        accountId = if (it.equals("Card", true)) cards.firstOrNull()?.first else bankAccounts.firstOrNull()?.first
                    },
                    label = { Text("Payment Source (Bank/Card/Wallet/Cash)") }
                )
                val selectedAccountList = if (source.equals("Card", true)) cards else bankAccounts
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    selectedAccountList.take(3).forEach { pair ->
                        TextButton(onClick = { accountId = pair.first }) {
                            Text(pair.second.take(8))
                        }
                    }
                }
                OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Reason (Optional)") })
                if (!error.isNullOrBlank()) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amountValue = amount.toDoubleOrNull()
                when {
                    person.isBlank() -> error = "Person required"
                    amountValue == null || amountValue <= 0.0 -> error = "Amount invalid"
                    type !in setOf("given", "taken") -> error = "Type should be given or taken"
                    else -> onSave(person, amountValue, type, category.ifBlank { "Miscellaneous" }, source, accountId, reason.ifBlank { null })
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun SettleDebtDialog(
    debt: Debt,
    bankAccounts: List<Pair<Long, String>>,
    cards: List<Pair<Long, String>>,
    onDismiss: () -> Unit,
    onSettle: (Double, String, Long?) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("Bank") }
    var accountId by remember { mutableStateOf<Long?>(bankAccounts.firstOrNull()?.first) }
    var error by remember { mutableStateOf<String?>(null) }
    val remaining = debt.amount - (debt.amountSettled ?: 0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settle Debt") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Remaining: INR ${"%.2f".format(remaining)}")
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Settlement Amount") }
                )
                OutlinedTextField(
                    value = source,
                    onValueChange = {
                        source = it
                        accountId = if (it.equals("Card", true)) cards.firstOrNull()?.first else bankAccounts.firstOrNull()?.first
                    },
                    label = { Text("Source (Bank/Card/Wallet/Cash)") }
                )
                val selectedList = if (source.equals("Card", true)) cards else bankAccounts
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    selectedList.take(3).forEach { pair ->
                        TextButton(onClick = { accountId = pair.first }) {
                            Text(pair.second.take(8))
                        }
                    }
                }
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
                    amountValue > remaining -> error = "Amount exceeds remaining"
                    else -> onSettle(amountValue, source, accountId)
                }
            }) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
