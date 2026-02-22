package com.udhaarpay.app.ui.screens.wallet

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.local.entities.Expense
import com.udhaarpay.app.ui.viewmodel.WalletViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletManagementScreen(viewModel: WalletViewModel = hiltViewModel()) {
    val wallet by viewModel.walletAccount.collectAsState()
    val transactions by viewModel.walletTransactions.collectAsState()
    val status by viewModel.statusMessage.collectAsState()
    var action by remember { mutableStateOf<WalletAction?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Cash Wallet", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Available Balance", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "INR ${"%.2f".format(wallet?.balance ?: 0.0)}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { action = WalletAction.Add }) { Text("Add Money") }
                    Button(onClick = { action = WalletAction.Withdraw }) { Text("Withdraw") }
                    Button(onClick = { action = WalletAction.Spend }) { Text("Spend") }
                }
            }
        }
        if (!status.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(status ?: "", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
            TextButton(onClick = { viewModel.clearStatus() }) { Text("Dismiss") }
        }
        Spacer(Modifier.height(10.dp))
        Text("Wallet Transactions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(transactions) { expense ->
                WalletTxnRow(expense = expense)
            }
        }
    }

    action?.let { walletAction ->
        WalletActionDialog(
            action = walletAction,
            onDismiss = { action = null },
            onSubmit = { amount, note ->
                when (walletAction) {
                    WalletAction.Add -> viewModel.addMoney(amount, note)
                    WalletAction.Withdraw -> viewModel.withdrawMoney(amount, note)
                    WalletAction.Spend -> viewModel.spendMoney(amount, note)
                }
                action = null
            }
        )
    }
}

@Composable
private fun WalletTxnRow(expense: Expense) {
    val isCredit = expense.category.equals("Wallet Topup", true)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(expense.category, fontWeight = FontWeight.SemiBold)
            if (!expense.description.isNullOrBlank()) {
                Text(expense.description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Text(
                SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(expense.date)),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            Text(
                "${if (isCredit) "+" else "-"}INR ${"%.2f".format(expense.amount)}",
                color = if (isCredit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun WalletActionDialog(
    action: WalletAction,
    onDismiss: () -> Unit,
    onSubmit: (Double, String?) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(action.title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (!error.isNullOrBlank()) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amountValue = amount.toDoubleOrNull()
                if (amountValue == null || amountValue <= 0) {
                    error = "Enter a valid amount."
                } else {
                    onSubmit(amountValue, note.ifBlank { null })
                }
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private enum class WalletAction(val title: String) {
    Add("Add Money"),
    Withdraw("Withdraw Money"),
    Spend("Record Spend")
}
