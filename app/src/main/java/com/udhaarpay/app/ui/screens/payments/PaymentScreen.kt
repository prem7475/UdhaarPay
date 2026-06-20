package com.udhaarpay.app.ui.screens.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.ui.components.PremiumActionCard
import com.udhaarpay.app.ui.components.PremiumInfoCard
import com.udhaarpay.app.ui.components.PremiumPill
import com.udhaarpay.app.ui.components.PremiumScreen
import com.udhaarpay.app.ui.components.PremiumSectionHeader
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
        sourceId != null &&
        isPinValid

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
    val recentPayments = remember(payments) {
        payments.sortedByDescending { it.date }.take(10)
    }

    PremiumScreen {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                PremiumSectionHeader(
                    title = "Payments",
                    subtitle = "Send, request, scan, and move money with local mock storage"
                )
            }

            item {
                PremiumInfoCard {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth(0.68f)) {
                            Text("Your UPI ID", fontWeight = FontWeight.SemiBold)
                            Text(myUpiId, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Pick a source, enter a pin if required, and keep the flow readable.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                            Text("Wallet Limit", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            Text("INR ${"%.0f".format(walletPinFreeLimit)}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        PremiumActionCard(
                            title = "Scan & Pay",
                            subtitle = "QR, NFC, and merchant flows",
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("scan_pay") }
                        )
                        PremiumActionCard(
                            title = "Bill Pay",
                            subtitle = "Utilities and dues",
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("pay_bills") }
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        PremiumActionCard(
                            title = "Recharge",
                            subtitle = "Mobile, DTH, data",
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("mobile_recharge") }
                        )
                        PremiumActionCard(
                            title = "Bookings",
                            subtitle = "Tickets and plans",
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("bookings") }
                        )
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    PremiumPill(text = "Send Money", selected = selectedTab == 0, modifier = Modifier.weight(1f)) { selectedTab = 0 }
                    PremiumPill(text = "Request Money", selected = selectedTab == 1, modifier = Modifier.weight(1f)) { selectedTab = 1 }
                }
            }

            if (selectedTab == 0) {
                item {
                    PremiumInfoCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = recipientUpi,
                                onValueChange = {
                                    recipientUpi = it
                                    sendAttempted = false
                                },
                                label = { Text("Recipient UPI") },
                                singleLine = true,
                                isError = recipientError != null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (recipientError != null) {
                                Text(recipientError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }

                            val recentContacts = contacts.take(4)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                recentContacts.forEach { contact ->
                                    PremiumPill(
                                        text = contact.name,
                                        selected = recipientUpi == contact.upiId,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        recipientUpi = contact.upiId
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = sendAmount,
                                onValueChange = {
                                    sendAmount = sanitizeAmountInput(it)
                                    sendAttempted = false
                                },
                                label = { Text("Amount") },
                                singleLine = true,
                                isError = sendAmountError != null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (sendAmountError != null) {
                                Text(sendAmountError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                listOf(100, 250, 500, 1000).forEach { quickAmount ->
                                    PremiumPill(
                                        text = "INR $quickAmount",
                                        selected = sendAmount == quickAmount.toString(),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        sendAmount = quickAmount.toString()
                                        sendAttempted = false
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = category,
                                onValueChange = { category = it },
                                label = { Text("Category") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = sendNote,
                                onValueChange = { sendNote = it.take(80) },
                                label = { Text("Note") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    PremiumInfoCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Pay From", fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                PremiumPill(text = "Bank", selected = sourceType == "bank", modifier = Modifier.weight(1f)) { sourceType = "bank" }
                                PremiumPill(text = "Card", selected = sourceType == "card", modifier = Modifier.weight(1f)) { sourceType = "card" }
                                PremiumPill(text = "Wallet", selected = sourceType == "wallet", modifier = Modifier.weight(1f)) { sourceType = "wallet" }
                            }

                            if (sourceOptions.isEmpty()) {
                                Text(
                                    "No ${sourceType.replaceFirstChar { it.uppercase(Locale.getDefault()) }} source available. Add one first.",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    sourceOptions.take(3).forEach { option ->
                                        PremiumPill(
                                            text = option.second,
                                            selected = sourceId == option.first,
                                            modifier = Modifier.fillMaxWidth()
                                        ) { sourceId = option.first }
                                    }
                                }
                            }

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
                                    "UPI PIN is required for the selected source.",
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
                                Text(pinError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
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
                                if (!canSelfTransfer || sendAmountValue == null) return@UdhaarPayButton
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
                }
            } else {
                item {
                    PremiumInfoCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = requesterUpi,
                                onValueChange = {
                                    requesterUpi = it
                                    requestAttempted = false
                                },
                                label = { Text("Requester UPI") },
                                singleLine = true,
                                isError = requesterError != null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (requesterError != null) {
                                Text(requesterError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }

                            OutlinedTextField(
                                value = requestAmount,
                                onValueChange = {
                                    requestAmount = sanitizeAmountInput(it)
                                    requestAttempted = false
                                },
                                label = { Text("Amount") },
                                singleLine = true,
                                isError = requestAmountError != null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (requestAmountError != null) {
                                Text(requestAmountError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }

                            OutlinedTextField(
                                value = requestNote,
                                onValueChange = { requestNote = it.take(80) },
                                label = { Text("Message") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    PremiumInfoCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Request preview", fontWeight = FontWeight.SemiBold)
                            Text("We keep the request flow compact so it stays easy to tap through on a phone.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
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
                        }
                    }
                }

                item {
                    PremiumInfoCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Recent Requests", fontWeight = FontWeight.SemiBold)
                            val pendingRequests = payments
                                .filter { it.type.equals("request", true) && !it.status.equals("received", true) }
                                .sortedByDescending { it.date }

                            if (pendingRequests.isEmpty()) {
                                Text("No pending requests right now.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            } else {
                                pendingRequests.take(3).forEach { req ->
                                    PremiumInfoCard {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.fillMaxWidth(0.72f)) {
                                                Text(req.senderUPI, fontWeight = FontWeight.SemiBold)
                                                Text("INR ${"%.2f".format(req.amount)}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                            }
                                            UdhaarPayButton(
                                                text = "Received",
                                                onClick = { viewModel.markRequestAsReceived(req) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!statusMessage.isNullOrBlank()) {
                item {
                    PremiumInfoCard {
                        Text(statusMessage.orEmpty(), color = MaterialTheme.colorScheme.primary)
                        UdhaarPayTextButton(text = "Dismiss", onClick = { viewModel.clearStatus() })
                    }
                }
            }

            item {
                PremiumSectionHeader(
                    title = "Recent Payments",
                    subtitle = "Your UPI: $myUpiId"
                )
            }

            items(
                items = recentPayments,
                key = { it.transactionId }
            ) { payment ->
                PremiumInfoCard {
                    Text(
                        "${payment.type.uppercase(Locale.getDefault())} | ${payment.status}",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("To/From: ${payment.recipientUPI}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text("Amount: INR ${"%.2f".format(payment.amount)}", color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(payment.date)),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
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
}
