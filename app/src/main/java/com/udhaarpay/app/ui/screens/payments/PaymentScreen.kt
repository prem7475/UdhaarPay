package com.udhaarpay.app.ui.screens.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.components.UdhaarPayTextButton
import com.udhaarpay.app.ui.viewmodel.UPIPaymentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val UPI_ID_REGEX = Regex("^[a-zA-Z0-9._-]{2,}@[a-zA-Z0-9.-]{2,}$")

private data class PaymentConfirmation(
    val title: String,
    val message: String,
    val onConfirm: () -> Unit
)

private fun sanitizeAmountInput(value: String): String {
    val filtered = value.filter { it.isDigit() || it == '.' }
    if (filtered.isEmpty()) return ""
    val firstDot = filtered.indexOf('.')
    return if (firstDot == -1) {
        filtered.take(7)
    } else {
        val beforeDot = filtered.substring(0, firstDot).take(7)
        val afterDot = filtered.substring(firstDot + 1).replace(".", "").take(2)
        if (beforeDot.isEmpty()) "0.$afterDot" else "$beforeDot.$afterDot"
    }
}

private fun isValidUpiId(upiId: String): Boolean = UPI_ID_REGEX.matches(upiId.trim())

@Composable
fun PaymentScreen(
    onNavigate: (String) -> Unit,
    viewModel: UPIPaymentViewModel = hiltViewModel()
) {
    val payments by viewModel.payments.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val bankAccounts by viewModel.bankAccounts.collectAsState()
    val cards by viewModel.creditCards.collectAsState()
    val wallet by viewModel.walletAccount.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    val myUpiId = currentUser?.upiId?.ifBlank { null } ?: "me@udhaarpay"
    var selectedTab by remember { mutableIntStateOf(0) }

    var recipientUpi by remember { mutableStateOf("") }
    var requesterUpi by remember { mutableStateOf("") }
    var sendAmount by remember { mutableStateOf("") }
    var requestAmount by remember { mutableStateOf("") }
    var sendNote by remember { mutableStateOf("") }
    var requestNote by remember { mutableStateOf("") }
    var sourceType by remember { mutableStateOf("bank") }
    var sourceId by remember { mutableStateOf<Long?>(null) }
    var category by remember { mutableStateOf("Miscellaneous") }
    var pin by remember { mutableStateOf("") }
    var sendAttempted by remember { mutableStateOf(false) }
    var requestAttempted by remember { mutableStateOf(false) }
    var confirmation by remember { mutableStateOf<PaymentConfirmation?>(null) }
    val walletPinFreeLimit = currentUser?.walletPinFreeLimit ?: 200.0

    val sourceOptions = remember(sourceType, bankAccounts, cards, wallet) {
        when (sourceType) {
        "bank" -> bankAccounts
            .filter { !it.accountType.equals("Wallet", true) }
            .map { it.accountId to "${it.bankName} ${it.accountNumber}" }

        "card" -> cards.map { it.cardId to "${it.issuer} ****${it.cardNumber} (${it.cardType})" }
        else -> listOfNotNull(wallet?.let { it.accountId to "${it.bankName} ${it.accountNumber}" })
        }
    }

    LaunchedEffect(sourceType, sourceOptions) {
        sourceId = sourceOptions.firstOrNull { it.first == sourceId }?.first
            ?: sourceOptions.firstOrNull()?.first
    }

    val normalizedRecipientUpi = recipientUpi.trim()
    val normalizedRequesterUpi = requesterUpi.trim()
    val sendAmountValue = sendAmount.toDoubleOrNull()
    val requestAmountValue = requestAmount.toDoubleOrNull()

    val recipientError = when {
        !sendAttempted && normalizedRecipientUpi.isBlank() -> null
        normalizedRecipientUpi.isBlank() -> "Recipient UPI is required."
        !isValidUpiId(normalizedRecipientUpi) -> "Enter a valid UPI ID like name@bank."
        else -> null
    }

    val sendAmountError = when {
        !sendAttempted && sendAmount.isBlank() -> null
        sendAmount.isBlank() -> "Amount is required."
        sendAmountValue == null || sendAmountValue <= 0.0 -> "Enter an amount greater than zero."
        sendAmountValue > 500000.0 -> "Amount should be less than or equal to INR 500000."
        else -> null
    }

    val pinRequired = sourceType == "bank" || sourceType == "card" || (
        sourceType == "wallet" && (sendAmountValue ?: 0.0) > walletPinFreeLimit
    )
    val pinError = when {
        !sendAttempted && pin.isBlank() -> null
        pinRequired && pin.length !in 4..6 -> "Enter a valid 4 to 6 digit UPI PIN."
        pin.isNotBlank() && pin.length !in 4..6 -> "UPI PIN must be 4 to 6 digits."
        else -> null
    }

    val isPinValid = when {
        pinRequired -> pin.length in 4..6
        pin.isBlank() -> true
        else -> pin.length in 4..6
    }

    val canSend = normalizedRecipientUpi.isNotBlank() &&
        isValidUpiId(normalizedRecipientUpi) &&
        sendAmountValue != null &&
        sendAmountValue > 0.0 &&
        sendAmountValue <= 500000.0 &&
        sourceOptions.isNotEmpty() &&
        sourceId != null
        && isPinValid

    val canSelfTransfer = sendAmountValue != null &&
        sendAmountValue > 0.0 &&
        sendAmountValue <= 500000.0 &&
        sourceOptions.isNotEmpty() &&
        sourceId != null &&
        isPinValid

    val requesterError = when {
        !requestAttempted && normalizedRequesterUpi.isBlank() -> null
        normalizedRequesterUpi.isBlank() -> "Requester UPI is required."
        !isValidUpiId(normalizedRequesterUpi) -> "Enter a valid UPI ID like name@bank."
        else -> null
    }

    val requestAmountError = when {
        !requestAttempted && requestAmount.isBlank() -> null
        requestAmount.isBlank() -> "Amount is required."
        requestAmountValue == null || requestAmountValue <= 0.0 -> "Enter an amount greater than zero."
        requestAmountValue > 500000.0 -> "Amount should be less than or equal to INR 500000."
        else -> null
    }

    val canRequest = normalizedRequesterUpi.isNotBlank() &&
        isValidUpiId(normalizedRequesterUpi) &&
        requestAmountValue != null &&
        requestAmountValue > 0.0 &&
        requestAmountValue <= 500000.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Payments", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            UdhaarPayButton(text = "Scan & Pay", onClick = { onNavigate("scan_pay") })
            UdhaarPayButton(text = "Bill Pay", onClick = { onNavigate("pay_bills") })
            UdhaarPayButton(text = "Recharge", onClick = { onNavigate("mobile_recharge") })
        }

        Spacer(Modifier.height(12.dp))
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Send Money") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Request Money") })
        }

        Spacer(Modifier.height(10.dp))
        if (selectedTab == 0) {
            OutlinedTextField(
                value = recipientUpi,
                onValueChange = { recipientUpi = it },
                label = { Text("Recipient UPI") },
                singleLine = true,
                isError = recipientError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            if (recipientError != null) {
                Text(
                    recipientError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                contacts.take(3).forEach { contact ->
                    Card(
                        onClick = { recipientUpi = contact.upiId },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(contact.name, modifier = Modifier.padding(8.dp), fontSize = 11.sp)
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = sendAmount,
                onValueChange = { sendAmount = sanitizeAmountInput(it) },
                label = { Text("Amount") },
                singleLine = true,
                isError = sendAmountError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            if (sendAmountError != null) {
                Text(
                    sendAmountError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(100, 250, 500, 1000).forEach { quickAmount ->
                    Card(
                        onClick = {
                            sendAmount = quickAmount.toString()
                            sendAttempted = false
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            "INR $quickAmount",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = sendNote,
                onValueChange = { sendNote = it.take(80) },
                label = { Text("Note") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                SourceChip(title = "Bank", selected = sourceType == "bank", onClick = {
                    sourceType = "bank"
                })
                SourceChip(title = "Card", selected = sourceType == "card", onClick = {
                    sourceType = "card"
                })
                SourceChip(title = "Wallet", selected = sourceType == "wallet", onClick = {
                    sourceType = "wallet"
                })
            }
            Spacer(Modifier.height(6.dp))
            if (sourceOptions.isEmpty()) {
                Text(
                    "No ${sourceType.replaceFirstChar { it.uppercase(Locale.getDefault()) }} source available. Add one first.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    sourceOptions.take(3).forEach { option ->
                        Card(
                            onClick = { sourceId = option.first },
                            colors = CardDefaults.cardColors(
                                containerColor = if (sourceId == option.first) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(option.second, modifier = Modifier.padding(8.dp), fontSize = 11.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it.filter(Char::isDigit).take(6) },
                label = { Text("UPI PIN") },
                singleLine = true,
                isError = pinError != null,
                visualTransformation = if (pin.isBlank()) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth()
            )
            if (pinRequired) {
                Text(
                    "UPI PIN is required for selected source.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            } else if (sourceType == "wallet") {
                Text(
                    "PIN optional up to INR ${"%.0f".format(walletPinFreeLimit)} from wallet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            if (pinError != null) {
                Text(
                    pinError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UdhaarPayButton(
                    text = "Send Payment",
                    onClick = {
                        sendAttempted = true
                        if (!canSend || sendAmountValue == null) return@UdhaarPayButton
                        confirmation = PaymentConfirmation(
                            title = "Confirm Payment",
                            message = "Send INR ${"%.2f".format(sendAmountValue)} to $normalizedRecipientUpi?",
                            onConfirm = {
                                viewModel.payToUpi(
                                    recipientUpi = normalizedRecipientUpi,
                                    amount = sendAmountValue,
                                    sourceType = sourceType,
                                    sourceId = sourceId,
                                    category = category,
                                    note = sendNote,
                                    enteredPin = pin
                                )
                                pin = ""
                            }
                        )
                    },
                    enabled = canSend,
                    modifier = Modifier.weight(1f)
                )
                UdhaarPayButton(
                    text = "Self Transfer",
                    onClick = {
                        sendAttempted = true
                        if (!canSelfTransfer || sendAmountValue == null) {
                            return@UdhaarPayButton
                        }
                        confirmation = PaymentConfirmation(
                            title = "Confirm Self Transfer",
                            message = "Transfer INR ${"%.2f".format(sendAmountValue)} to your wallet?",
                            onConfirm = {
                                viewModel.transferToSelfWallet(
                                    amount = sendAmountValue,
                                    sourceType = sourceType,
                                    sourceId = sourceId,
                                    note = sendNote,
                                    enteredPin = pin
                                )
                                pin = ""
                            }
                        )
                    },
                    enabled = canSelfTransfer,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            val pendingRequests = payments
                .filter { it.type.equals("request", true) && !it.status.equals("received", true) }
                .sortedByDescending { it.date }

            OutlinedTextField(
                value = requesterUpi,
                onValueChange = { requesterUpi = it },
                label = { Text("Requester UPI") },
                singleLine = true,
                isError = requesterError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            if (requesterError != null) {
                Text(
                    requesterError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = requestAmount,
                onValueChange = { requestAmount = sanitizeAmountInput(it) },
                label = { Text("Amount") },
                singleLine = true,
                isError = requestAmountError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            if (requestAmountError != null) {
                Text(
                    requestAmountError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = requestNote,
                onValueChange = { requestNote = it.take(80) },
                label = { Text("Message") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            UdhaarPayButton(
                text = "Request Money",
                onClick = {
                    requestAttempted = true
                    if (!canRequest || requestAmountValue == null) return@UdhaarPayButton
                    confirmation = PaymentConfirmation(
                        title = "Confirm Request",
                        message = "Request INR ${"%.2f".format(requestAmountValue)} from $normalizedRequesterUpi?",
                        onConfirm = {
                            viewModel.requestMoney(
                                requesterUpi = normalizedRequesterUpi,
                                amount = requestAmountValue,
                                note = requestNote
                            )
                        }
                    )
                },
                enabled = canRequest,
                modifier = Modifier.fillMaxWidth()
            )

            if (pendingRequests.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text("Pending Requests", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                pendingRequests.take(4).forEach { req ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(req.senderUPI, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("INR ${"%.2f".format(req.amount)}", color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(6.dp))
                            UdhaarPayButton(
                                text = "Mark Received",
                                onClick = { viewModel.markRequestAsReceived(req) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        confirmation?.let { action ->
            AlertDialog(
                onDismissRequest = { confirmation = null },
                title = { Text(action.title) },
                text = { Text(action.message) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            action.onConfirm()
                            confirmation = null
                        }
                    ) { Text("Confirm") }
                },
                dismissButton = {
                    TextButton(onClick = { confirmation = null }) { Text("Cancel") }
                }
            )
        }

        if (!statusMessage.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(statusMessage ?: "", color = MaterialTheme.colorScheme.primary)
            UdhaarPayTextButton(text = "Dismiss", onClick = { viewModel.clearStatus() })
        }

        Spacer(Modifier.height(14.dp))
        Text("Recent Payments", fontWeight = FontWeight.SemiBold)
        Text("Your UPI: $myUpiId", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        val recentPayments = remember(payments) {
            payments.sortedByDescending { it.date }.take(10)
        }

        if (recentPayments.isEmpty()) {
            Text("No transactions yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recentPayments.forEach { payment ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                "${payment.type.uppercase(Locale.getDefault())} | ${payment.status}",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text("To/From: ${payment.recipientUPI}")
                            Text("Amount: INR ${"%.2f".format(payment.amount)}")
                            Text(
                                SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                    .format(Date(payment.date)),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceChip(title: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(title, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), fontSize = 12.sp)
    }
}
