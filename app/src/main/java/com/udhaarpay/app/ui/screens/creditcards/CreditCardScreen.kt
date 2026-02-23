package com.udhaarpay.app.ui.screens.creditcards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.udhaarpay.app.data.local.entities.CreditCard
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.screens.nfc.NFCPaymentScreen
import com.udhaarpay.app.ui.viewmodel.BankAccountViewModel
import com.udhaarpay.app.ui.viewmodel.CreditCardViewModel
import com.udhaarpay.app.ui.viewmodel.NFCPaymentViewModel

private data class Coupon(val title: String, val category: String, val code: String)

@Composable
fun CreditCardScreen(
    viewModel: CreditCardViewModel = hiltViewModel(),
    bankAccountViewModel: BankAccountViewModel = hiltViewModel(),
    nfcPaymentViewModel: NFCPaymentViewModel = hiltViewModel(),
    onNavigateHome: () -> Unit = {}
) {
    val cards by viewModel.creditCards.collectAsState()
    val status by viewModel.statusMessage.collectAsState()
    val bankAccounts by bankAccountViewModel.accounts.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var cardNumber by remember { mutableStateOf("") }
    var issuer by remember { mutableStateOf("HDFC Bank") }
    var expiry by remember { mutableStateOf("12/29") }
    var limit by remember { mutableStateOf("100000") }
    var selectedCardIdForBill by remember { mutableStateOf<Long?>(cards.firstOrNull()?.cardId) }
    var selectedBankIdForBill by remember { mutableStateOf<Long?>(bankAccounts.firstOrNull()?.accountId) }
    var billAmount by remember { mutableStateOf("") }
    var searchCoupon by remember { mutableStateOf("") }
    var showNfcDialog by remember { mutableStateOf(false) }
    var revealedCardIds by remember { mutableStateOf(setOf<Long>()) }
    var cardToReveal by remember { mutableStateOf<CreditCard?>(null) }
    val detectedType = if (cardNumber.isBlank()) "-" else viewModel.detectCardType(cardNumber)

    val coupons = listOf(
        Coupon("Dining Gold 20%", "Dining", "DINE20"),
        Coupon("Travel Reward 15%", "Travel", "TRAVEL15"),
        Coupon("Premium Retail 10%", "Shopping", "LUXE10"),
        Coupon("Movie Night Offer", "Entertainment", "MOVIE100")
    )
    val filteredCoupons = coupons.filter {
        it.title.contains(searchCoupon, true) || it.category.contains(searchCoupon, true) || it.code.contains(searchCoupon, true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Credit Cards", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("My Cards") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Pay Bill") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Cashback") })
        }
        Spacer(Modifier.height(10.dp))

        when (selectedTab) {
            0 -> {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("Add Card", fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it.filter(Char::isDigit).take(16) },
                            label = { Text("Card Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "Detected: $detectedType | NFC ${if (detectedType.equals("RuPay", true)) "Eligible" else "RuPay only"}",
                            color = if (detectedType.equals("RuPay", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = issuer,
                            onValueChange = { issuer = it },
                            label = { Text("Issuer") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = expiry,
                            onValueChange = { expiry = it },
                            label = { Text("Expiry MM/YY") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = limit,
                            onValueChange = { limit = it.filter { c -> c.isDigit() || c == '.' } },
                            label = { Text("Credit Limit") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        UdhaarPayButton(
                            text = "Pay with NFC",
                            onClick = { showNfcDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        UdhaarPayButton(
                            text = "Add Card",
                            onClick = {
                                val limitValue = limit.toDoubleOrNull()
                                if (limitValue != null && limitValue > 0.0) {
                                    viewModel.addCardWithDetection(cardNumber, issuer, expiry, limitValue)
                                    cardNumber = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items = cards, key = { it.cardId }) { card ->
                        val isRevealed = card.cardId in revealedCardIds
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${card.issuer} | ${card.cardType}", fontWeight = FontWeight.SemiBold)
                                        Text("**** ${card.cardNumber}")
                                        if (isRevealed) {
                                            val outstanding = (card.limit - card.balance).coerceAtLeast(0.0)
                                            Text("Outstanding: INR ${"%.2f".format(outstanding)}")
                                            Text("Limit: INR ${"%.2f".format(card.limit)}")
                                        } else {
                                            Text("Outstanding: INR •••••")
                                            Text("Limit: INR •••••")
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            if (isRevealed) {
                                                revealedCardIds = revealedCardIds - card.cardId
                                            } else {
                                                cardToReveal = card
                                            }
                                        }
                                    ) {
                                        Icon(
                                            if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Reveal"
                                        )
                                    }
                                    IconButton(onClick = { viewModel.delete(card) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                                val utilization = ((card.limit - card.balance) / card.limit).toFloat().coerceIn(0f, 1f)
                                if (isRevealed) {
                                    LinearProgressIndicator(progress = { utilization }, modifier = Modifier.fillMaxWidth())
                                    Text("Utilization: ${"%.1f".format(utilization * 100)}%", fontSize = 12.sp)
                                } else {
                                    Text("Utilization: PIN protected", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("UPI Link", modifier = Modifier.weight(1f))
                                    Switch(
                                        checked = card.upiLinked,
                                        onCheckedChange = { checked ->
                                            viewModel.toggleUpiLink(card, checked)
                                        }
                                    )
                                }
                                if (!card.cardType.equals("rupay", true)) {
                                    Text("NFC eligible only for RuPay cards.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("Pay Credit Card Bill", fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = cards.firstOrNull { it.cardId == selectedCardIdForBill }?.let {
                                "${it.issuer} ${it.cardNumber}"
                            } ?: "No card",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Selected Card") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            cards.take(3).forEach { card ->
                                TextButton(onClick = { selectedCardIdForBill = card.cardId }) {
                                    Text(card.cardNumber)
                                }
                            }
                        }
                        OutlinedTextField(
                            value = bankAccounts.firstOrNull { it.accountId == selectedBankIdForBill }?.bankName ?: "No Bank",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Paying Bank") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            bankAccounts.take(3).forEach { bank ->
                                TextButton(onClick = { selectedBankIdForBill = bank.accountId }) {
                                    Text(bank.bankName.take(8))
                                }
                            }
                        }
                        OutlinedTextField(
                            value = billAmount,
                            onValueChange = { billAmount = it.filter { c -> c.isDigit() || c == '.' } },
                            label = { Text("Bill Amount") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        UdhaarPayButton(
                            text = "Pay Bill",
                            onClick = {
                                val card = cards.firstOrNull { it.cardId == selectedCardIdForBill }
                                val bankId = selectedBankIdForBill
                                val amt = billAmount.toDoubleOrNull()
                                if (card != null && bankId != null && amt != null) {
                                    viewModel.payCreditCardBill(card, bankId, amt)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            2 -> {
                val cashbackEarned = cards.sumOf { (it.limit - it.balance).coerceAtLeast(0.0) * 0.01 }
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("Cashback", fontWeight = FontWeight.SemiBold)
                        Text("Earned: INR ${"%.2f".format(cashbackEarned)}")
                        OutlinedTextField(
                            value = searchCoupon,
                            onValueChange = { searchCoupon = it },
                            label = { Text("Search offers") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        items = filteredCoupons,
                        key = { "${it.code}-${it.title}" }
                    ) { coupon ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                Text(coupon.title, fontWeight = FontWeight.SemiBold)
                                Text("Category: ${coupon.category}")
                                Text("Code: ${coupon.code}", color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }
        }

        if (!status.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(status ?: "", color = MaterialTheme.colorScheme.primary)
            TextButton(onClick = { viewModel.clearStatusMessage() }) { Text("Dismiss") }
        }
    }

    if (showNfcDialog) {
        Dialog(onDismissRequest = { showNfcDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Box(Modifier.sizeIn(minWidth = 340.dp, minHeight = 520.dp)) {
                    NFCPaymentScreen(
                        onBack = { showNfcDialog = false },
                        onReturnHomeAfterSuccess = {
                            showNfcDialog = false
                            onNavigateHome()
                        },
                        nfcViewModel = nfcPaymentViewModel
                    )
                }
            }
        }
    }

    cardToReveal?.let { card ->
        var pin by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { cardToReveal = null },
            title = { Text("Enter UPI PIN") },
            text = {
                Column {
                    Text("Reveal details for ${card.issuer} ••••${card.cardNumber}")
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
                TextButton(
                    onClick = {
                        val isValidPin = bankAccounts.any { !it.upiPin.isNullOrBlank() && it.upiPin == pin }
                        if (isValidPin) {
                            revealedCardIds = revealedCardIds + card.cardId
                            cardToReveal = null
                        } else {
                            error = "Invalid UPI PIN"
                        }
                    }
                ) { Text("Reveal") }
            },
            dismissButton = {
                TextButton(onClick = { cardToReveal = null }) { Text("Cancel") }
            }
        )
    }
}
