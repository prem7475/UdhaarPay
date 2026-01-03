package com.example.udhaarpay.ui.screens.scanpay

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.ScanPayViewModel

@Composable
fun ScanPayScreen(
    onBack: () -> Unit,
    viewModel: ScanPayViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var upiString by remember { mutableStateOf("upi://pay?pa=merchant@upi&pn=MerchantName&mcc=1234") }
    
    // Display error message from ViewModel
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    // Success Navigation
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            // Wait a bit to show success animation then navigate back
            kotlinx.coroutines.delay(1500)
            onBack()
        }
    }

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            SmallTopAppBar(
                title = { Text("Scan & Pay", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = PureBlack)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Main Content
            if (!state.isSuccess) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. QR Scanner Simulation
                    if (!state.isQrProcessed) {
                        Text("Simulate QR Scan", color = Zinc400, style = MaterialTheme.typography.labelMedium)
                        TextField(
                            value = upiString,
                            onValueChange = { upiString = it },
                            label = { Text("UPI String") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DarkZinc,
                                unfocusedContainerColor = DarkZinc,
                                focusedTextColor = White,
                                unfocusedTextColor = White,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Zinc800
                            ),
                            trailingIcon = {
                                IconButton(onClick = { viewModel.processQrCode(upiString) }) {
                                    Icon(Icons.Default.QrCode, contentDescription = "Scan", tint = PrimaryBlue)
                                }
                            }
                        )
                        Button(
                            onClick = { viewModel.processQrCode(upiString) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Process QR Code")
                        }
                    }

                    // 2. Payment Flow
                    if (state.isQrProcessed) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkZinc),
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Paying To", color = Zinc400, style = MaterialTheme.typography.labelMedium)
                                Text(
                                    text = state.payeeName,
                                    color = White,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = state.payeeVpa,
                                    color = Zinc400,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (state.isMerchant) "Merchant Payment" else "Personal Transfer",
                                    color = if (state.isMerchant) PrimaryBlue else Color.Green,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        
                        // Amount Input
                        OutlinedTextField(
                            value = state.amount,
                            onValueChange = { viewModel.setAmount(it) },
                            label = { Text("Amount") },
                            prefix = { Text("₹ ", color = White) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = White,
                                unfocusedTextColor = White,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Zinc800
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        )

                        Text("Select Payment Source", color = Zinc400, style = MaterialTheme.typography.labelMedium)
                        
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Bank Accounts List
                            items(state.bankAccounts) { account ->
                                PaymentSourceItem(
                                    title = account.bankName,
                                    subtitle = "Acct: ****${account.accountNumber.takeLast(4)}",
                                    balance = "₹${String.format("%.2f", account.balance)}",
                                    isSelected = state.selectedSourceId == "bank_${account.id}",
                                    onClick = { viewModel.selectPaymentSource("bank_${account.id}") }
                                )
                            }

                            // Credit Cards List
                            items(state.creditCards) { card ->
                                val isEnabled = state.isMerchant || card.isRupay
                                val opacity = if (isEnabled) 1f else 0.5f
                                
                                PaymentSourceItem(
                                    title = card.name,
                                    subtitle = if (card.isRupay) "Rupay Credit Card" else "Credit Card",
                                    balance = "Limit: ₹${String.format("%.0f", card.limit)}",
                                    isSelected = state.selectedSourceId == "card_${card.id}",
                                    isEnabled = isEnabled,
                                    onClick = { 
                                        if (isEnabled) {
                                            viewModel.selectPaymentSource("card_${card.id}")
                                        } else {
                                            Toast.makeText(context, "Credit cards not allowed for personal transfers", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.alpha(opacity)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Button(
                            onClick = { viewModel.processPayment() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            enabled = state.selectedSourceId != null && state.amount.isNotEmpty() && !state.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Pay Now")
                        }
                    }
                }
            }

            // 3. Loading Overlay
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PureBlack.copy(alpha = 0.8f))
                        .clickable(enabled = false) {}, // Block interaction
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Processing Payment...", color = White, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // 4. Success Animation
            AnimatedVisibility(
                visible = state.isSuccess,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PureBlack)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Green, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Success",
                            tint = White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Payment Successful!",
                        color = White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "₹${state.amount} sent to ${state.payeeName}",
                        color = Zinc400,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentSourceItem(
    title: String,
    subtitle: String,
    balance: String,
    isSelected: Boolean,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled, onClick = onClick)
            .background(if (isSelected) Zinc800 else DarkZinc, RoundedCornerShape(12.dp))
            .border(1.dp, if (isSelected) PrimaryBlue else Zinc800, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CreditCard,
            contentDescription = null,
            tint = if(isSelected) PrimaryBlue else Zinc400
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = White, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Zinc400, style = MaterialTheme.typography.labelSmall)
        }
        Text(balance, color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
