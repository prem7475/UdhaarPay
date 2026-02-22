package com.udhaarpay.app.ui.screens.payments

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.components.UdhaarPayTextButton
import com.udhaarpay.app.ui.viewmodel.UPIPaymentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var sourceType by remember { mutableStateOf("bank") }
    var sourceId by remember { mutableStateOf<Long?>(null) }
    var category by remember { mutableStateOf("Miscellaneous") }
    var pin by remember { mutableStateOf("") }

    val sourceOptions = when (sourceType) {
        "bank" -> bankAccounts
            .filter { !it.accountType.equals("Wallet", true) }
            .map { it.accountId to "${it.bankName} ${it.accountNumber}" }

        "card" -> cards.map { it.cardId to "${it.issuer} ****${it.cardNumber} (${it.cardType})" }
        else -> listOfNotNull(wallet?.let { it.accountId to "${it.bankName} ${it.accountNumber}" })
    }

    if (sourceId == null && sourceOptions.isNotEmpty()) {
        sourceId = sourceOptions.first().first
    }

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
                modifier = Modifier.fillMaxWidth()
            )
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
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                SourceChip(title = "Bank", selected = sourceType == "bank", onClick = {
                    sourceType = "bank"
                    sourceId = sourceOptions.firstOrNull()?.first
                })
                SourceChip(title = "Card", selected = sourceType == "card", onClick = {
                    sourceType = "card"
                    sourceId = sourceOptions.firstOrNull()?.first
                })
                SourceChip(title = "Wallet", selected = sourceType == "wallet", onClick = {
                    sourceType = "wallet"
                    sourceId = sourceOptions.firstOrNull()?.first
                })
            }
            Spacer(Modifier.height(6.dp))
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
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it.filter(Char::isDigit).take(6) },
                label = { Text("UPI PIN") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UdhaarPayButton(
                    text = "Send Payment",
                    onClick = {
                        val amountValue = amount.toDoubleOrNull() ?: 0.0
                        viewModel.payToUpi(
                            recipientUpi = recipientUpi,
                            amount = amountValue,
                            sourceType = sourceType,
                            sourceId = sourceId,
                            category = category,
                            note = note,
                            enteredPin = pin
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                UdhaarPayButton(
                    text = "Self Transfer",
                    onClick = {
                        val amountValue = amount.toDoubleOrNull() ?: 0.0
                        viewModel.transferToSelfWallet(
                            amount = amountValue,
                            sourceType = sourceType,
                            sourceId = sourceId,
                            note = note,
                            enteredPin = pin
                        )
                    },
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
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            UdhaarPayButton(
                text = "Request Money",
                onClick = {
                    viewModel.requestMoney(
                        requesterUpi = requesterUpi,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        note = note
                    )
                },
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

        if (!statusMessage.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(statusMessage ?: "", color = MaterialTheme.colorScheme.primary)
            UdhaarPayTextButton(text = "Dismiss", onClick = { viewModel.clearStatus() })
        }

        Spacer(Modifier.height(14.dp))
        Text("Recent Payments", fontWeight = FontWeight.SemiBold)
        Text("Your UPI: $myUpiId", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        if (payments.isEmpty()) {
            Text("No transactions yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(payments.sortedByDescending { it.date }.take(10)) { payment ->
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
                            Text("Amount: INR ${payment.amount}")
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
