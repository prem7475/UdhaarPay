package com.udhaarpay.app.ui.screens.bankaccounts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.viewmodel.BankAccountViewModel

@Composable
fun BankAccountScreen(
    onOpenPassbook: (Long) -> Unit = {},
    viewModel: BankAccountViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val totalBalance by viewModel.totalBalance.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val loadingAccountIds by viewModel.loadingAccountIds.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var accountToEdit by remember { mutableStateOf<BankAccount?>(null) }
    var accountToReveal by remember { mutableStateOf<BankAccount?>(null) }
    var selectedAccountIds by remember { mutableStateOf(setOf<Long>()) }
    var revealedAccountIds by remember { mutableStateOf(setOf<Long>()) }

    val selectedTotalRaw = accounts
        .filter { it.accountId in selectedAccountIds && it.accountId in revealedAccountIds }
        .sumOf { it.balance }
    val animatedSelectedTotal by animateFloatAsState(
        targetValue = selectedTotalRaw.toFloat(),
        label = "selected_total"
    )

    val totalVisible = revealedAccountIds.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("My Bank Accounts", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                Text("Total Balance (All Linked Accounts)")
                if (totalVisible) {
                    Text(
                        "INR ${"%.2f".format(totalBalance)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                } else {
                    Text("INR ••••••", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Text("Tap reveal on any account to unmask balances.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
                Text("Accounts linked: ${accounts.size}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (selectedAccountIds.size >= 2) {
                    Spacer(Modifier.height(8.dp))
                    if (selectedAccountIds.all { it in revealedAccountIds }) {
                        Text(
                            "Selected (${selectedAccountIds.size}) Total: INR ${"%.2f".format(animatedSelectedTotal)}",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            "Selected total hidden (PIN protected).",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    UdhaarPayButton(text = "Add Account", onClick = { showAddDialog = true })
                    UdhaarPayButton(
                        text = "Check Balance",
                        onClick = {
                            if (selectedAccountIds.isEmpty()) {
                                viewModel.checkAllBalances()
                            } else {
                                accounts.filter { it.accountId in selectedAccountIds }
                                    .forEach { viewModel.checkBalance(it) }
                            }
                        }
                    )
                }
            }
        }

        if (!statusMessage.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(statusMessage ?: "", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
            TextButton(onClick = { viewModel.clearStatusMessage() }) {
                Text("Dismiss")
            }
        }

        Spacer(Modifier.height(10.dp))
        if (accounts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No bank accounts yet. Add your first account.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(accounts) { account ->
                    val isSelected = account.accountId in selectedAccountIds
                    val isLoading = account.accountId in loadingAccountIds
                    val isRevealed = account.accountId in revealedAccountIds

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    selectedAccountIds = if (checked) {
                                        selectedAccountIds + account.accountId
                                    } else {
                                        selectedAccountIds - account.accountId
                                    }
                                }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(account.bankName, fontWeight = FontWeight.Bold)
                                Text("${account.accountType} | ${account.accountNumber}")
                                Text("IFSC: ${account.ifscCode}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "Nickname: ${account.nickname ?: "-"}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                                Text(
                                    if (isRevealed) {
                                        "Balance: INR ${"%.2f".format(account.balance)}"
                                    } else {
                                        "Balance: INR ••••••"
                                    }
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                                } else {
                                    TextButton(onClick = { viewModel.checkBalance(account) }) {
                                        Text("Check")
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        if (isRevealed) {
                                            revealedAccountIds = revealedAccountIds - account.accountId
                                        } else {
                                            accountToReveal = account
                                        }
                                    }
                                ) {
                                    Icon(
                                        if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle balance visibility"
                                    )
                                }
                                Row {
                                    IconButton(onClick = { accountToEdit = account }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit nickname")
                                    }
                                    IconButton(onClick = { viewModel.deleteBankAccount(account) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                                TextButton(onClick = { onOpenPassbook(account.accountId) }) {
                                    Text("Passbook")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBankAccountDialog(
            banks = viewModel.availableBanks(),
            onAdd = { bankName, accountNumber, ifsc, accountType, upiPin, nickname ->
                val sanitizedNumber = accountNumber.takeLast(4).padStart(8, 'X')
                viewModel.addBankAccount(
                    BankAccount(
                        bankName = bankName,
                        accountNumber = sanitizedNumber,
                        ifscCode = ifsc.uppercase(),
                        accountType = accountType,
                        balance = 0.0,
                        upiPin = upiPin,
                        nickname = nickname.ifBlank { null },
                        addedDate = System.currentTimeMillis()
                    )
                )
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    accountToEdit?.let { account ->
        EditNicknameDialog(
            currentNickname = account.nickname.orEmpty(),
            onSave = { nickname ->
                viewModel.updateNickname(account, nickname)
                accountToEdit = null
            },
            onDismiss = { accountToEdit = null }
        )
    }

    accountToReveal?.let { account ->
        RevealBalanceDialog(
            account = account,
            onDismiss = { accountToReveal = null },
            onReveal = { pin ->
                if (viewModel.verifyUpiPin(account.accountId, pin)) {
                    revealedAccountIds = revealedAccountIds + account.accountId
                    accountToReveal = null
                    true
                } else {
                    false
                }
            }
        )
    }
}

@Composable
private fun RevealBalanceDialog(
    account: BankAccount,
    onDismiss: () -> Unit,
    onReveal: (String) -> Boolean
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter UPI PIN") },
        text = {
            Column {
                Text("Reveal balance for ${account.bankName}")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it.filter(Char::isDigit).take(6) },
                    label = { Text("UPI PIN") },
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
                if (pin.length < 4) {
                    error = "Enter valid PIN"
                } else {
                    val success = onReveal(pin)
                    error = if (success) null else "Incorrect PIN"
                }
            }) { Text("Reveal") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun EditNicknameDialog(
    currentNickname: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var nickname by remember { mutableStateOf(currentNickname) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Nickname") },
        text = {
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nickname") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onSave(nickname.trim()) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun AddBankAccountDialog(
    banks: List<String>,
    onAdd: (String, String, String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedBank by remember { mutableStateOf(banks.firstOrNull().orEmpty()) }
    var accountNumber by remember { mutableStateOf("") }
    var ifsc by remember { mutableStateOf("") }
    var accountType by remember { mutableStateOf("Savings") }
    var upiPin by remember { mutableStateOf("") }
    var confirmUpiPin by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var bankMenuExpanded by remember { mutableStateOf(false) }
    var typeMenuExpanded by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bank Account") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Bank")
                Box {
                    OutlinedTextField(
                        value = selectedBank,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Select Bank") }
                    )
                    TextButton(onClick = { bankMenuExpanded = true }) { Text("Change") }
                    DropdownMenu(expanded = bankMenuExpanded, onDismissRequest = { bankMenuExpanded = false }) {
                        banks.forEach { bank ->
                            DropdownMenuItem(
                                text = { Text(bank) },
                                onClick = {
                                    selectedBank = bank
                                    bankMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it.filter(Char::isDigit) },
                    label = { Text("Account Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ifsc,
                    onValueChange = { ifsc = it.uppercase() },
                    label = { Text("IFSC Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Type: $accountType")
                    TextButton(onClick = { typeMenuExpanded = true }) { Text("Change") }
                    DropdownMenu(expanded = typeMenuExpanded, onDismissRequest = { typeMenuExpanded = false }) {
                        listOf("Savings", "Current", "Salary").forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    accountType = type
                                    typeMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = upiPin,
                    onValueChange = { upiPin = it.filter(Char::isDigit).take(6) },
                    label = { Text("Set UPI PIN (4-6 digits)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmUpiPin,
                    onValueChange = { confirmUpiPin = it.filter(Char::isDigit).take(6) },
                    label = { Text("Confirm UPI PIN") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Nickname (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (!errorText.isNullOrBlank()) {
                    Text(errorText ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val isValidIfsc = ifsc.matches(Regex("^[A-Z]{4}0[A-Z0-9]{6}$"))
                when {
                    selectedBank.isBlank() -> errorText = "Select a bank"
                    accountNumber.length < 8 -> errorText = "Account number must be at least 8 digits"
                    !isValidIfsc -> errorText = "Invalid IFSC format"
                    upiPin.length !in 4..6 -> errorText = "UPI PIN must be 4-6 digits"
                    upiPin != confirmUpiPin -> errorText = "UPI PIN mismatch"
                    else -> onAdd(selectedBank, accountNumber, ifsc, accountType, upiPin, nickname)
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
