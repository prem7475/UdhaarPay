package com.example.udhaarpay.ui.screens.scanpay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.data.model.BankCard
import com.example.udhaarpay.ui.components.*
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.ScanPayViewModel

enum class ScanPayStep {
    SCANNER,
    PAYMENT_DETAILS,
    ACCOUNT_SELECTION,
    UPI_PIN,
    CONFIRMATION
}

@Composable
fun ScanPayScreen(
    onBack: () -> Unit,
    viewModel: ScanPayViewModel = hiltViewModel()
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val qrData by viewModel.qrData.collectAsState()
    val recipientName by viewModel.recipientName.collectAsState()
    val recipientUPI by viewModel.recipientUPI.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val remarks by viewModel.remarks.collectAsState()
    val selectedAccount by viewModel.selectedAccount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground)
    ) {
        when (currentStep) {
            ScanPayStep.SCANNER -> {
                QRScannerStep(
                    onQRScanned = { scannedData ->
                        viewModel.processQRCode(scannedData)
                    },
                    onBack = onBack
                )
            }

            ScanPayStep.PAYMENT_DETAILS -> {
                PaymentDetailsStep(
                    recipientName = recipientName,
                    recipientUPI = recipientUPI,
                    amount = amount,
                    onAmountChange = { viewModel.setAmount(it) },
                    remarks = remarks,
                    onRemarksChange = { viewModel.setRemarks(it) },
                    onContinue = { viewModel.moveToAccountSelection() },
                    onBack = { viewModel.resetFlow() }
                )
            }

            ScanPayStep.ACCOUNT_SELECTION -> {
                AccountSelectionStep(
                    onAccountSelected = { accountId ->
                        viewModel.selectAccount(accountId)
                    },
                    onBack = { viewModel.goBackToPaymentDetails() }
                )
            }

            ScanPayStep.UPI_PIN -> {
                UPIPinStep(
                    onPinSubmit = { pin ->
                        viewModel.processPayment(pin)
                    },
                    onBack = { viewModel.goBackToAccountSelection() }
                )
            }

            ScanPayStep.CONFIRMATION -> {
                PaymentConfirmationStep(
                    amount = amount,
                    recipientName = recipientName,
                    remarks = remarks,
                    onNewTransaction = {
                        viewModel.resetFlow()
                    },
                    onClose = onBack
                )
            }
        }

        // Global loading dialog
        LoadingDialog(isVisible = isLoading, message = "Processing Payment...")

        // Error display
        if (error != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = ErrorRed,
                contentColor = Color.White
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(text = error!!)
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { viewModel.clearError() }
                    )
                }
            }
        }
    }
}

@Composable
fun QRScannerStep(
    onQRScanned: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground)
    ) {
        PremiumTopAppBar(
            title = "Scan QR Code",
            onBackClick = onBack
        )

        // Camera Preview Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(color = DarkCard),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "QR Code",
                    tint = NeonOrange,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Position QR code in frame",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Automatic detection enabled",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PremiumButton(
                text = "Import from Gallery",
                onClick = { },
                backgroundColor = DarkCard,
                textColor = NeonOrange
            )

            Text(
                text = "Scan any UPI QR code to make payments instantly",
                fontSize = 11.sp,
                color = TextTertiary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun PaymentDetailsStep(
    recipientName: String,
    recipientUPI: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    remarks: String,
    onRemarksChange: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground)
            .verticalScroll(rememberScrollState())
    ) {
        PremiumTopAppBar(
            title = "Payment Details",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Recipient Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        NeonOrange,
                                        NeonOrangeDark
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = recipientName.firstOrNull()?.toString() ?: "U",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = recipientName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = recipientUPI,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = SuccessGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Amount Input
            PremiumTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = "Enter Amount",
                placeholder = "₹ 0.00",
                keyboardType = KeyboardType.Number,
                leadingIcon = {
                    Text(
                        text = "₹",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonOrange
                    )
                }
            )

            // Remarks Input
            PremiumTextField(
                value = remarks,
                onValueChange = onRemarksChange,
                label = "Add Remark (Optional)",
                placeholder = "E.g., Payment for...",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            PremiumButton(
                text = "Continue",
                onClick = onContinue,
                enabled = amount.isNotEmpty() && amount.toDoubleOrNull() ?: 0.0 > 0
            )
        }
    }
}

@Composable
fun AccountSelectionStep(
    onAccountSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground)
    ) {
        PremiumTopAppBar(
            title = "Select Account",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Which account do you want to use?",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Mock accounts
            repeat(3) { index ->
                AccountSelectCard(
                    bankName = listOf("HDFC Bank", "ICICI Bank", "Axis Bank")[index],
                    accountNumber = "****" + listOf("1234", "5678", "9012")[index],
                    balance = "₹" + listOf("15,450", "8,900", "25,600")[index],
                    onClick = {
                        onAccountSelected("account_${index}")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "RuPay Card",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            AccountSelectCard(
                bankName = "RuPay Card",
                accountNumber = "•••• •••• •••• 4567",
                balance = "₹45,000",
                onClick = { onAccountSelected("rupaay_card") },
                isCard = true
            )
        }
    }
}

@Composable
fun AccountSelectCard(
    bankName: String,
    accountNumber: String,
    balance: String,
    onClick: () -> Unit,
    isCard: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (isCard) NeonOrange.copy(alpha = 0.2f)
                        else AccentBlue.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCard) Icons.Default.CreditCard else Icons.Default.Business,
                    contentDescription = if (isCard) "Card" else "Bank",
                    tint = if (isCard) NeonOrange else AccentBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = bankName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = accountNumber,
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = balance,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Check Balance",
                    fontSize = 10.sp,
                    color = NeonOrange,
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}

@Composable
fun UPIPinStep(
    onPinSubmit: (String) -> Unit,
    onBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var showPin by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PremiumTopAppBar(
            title = "Enter UPI PIN",
            onBackClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "PIN",
            tint = NeonOrange,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Enter your UPI PIN",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = "For secure payment verification",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // PIN Input Fields
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            repeat(4) { index ->
                OutlinedTextField(
                    value = pin.getOrNull(index)?.toString() ?: "",
                    onValueChange = { newValue ->
                        if (newValue.length <= 1) {
                            val newPin = pin.toMutableList()
                            if (newValue.isNotEmpty()) {
                                if (newPin.size <= index) {
                                    newPin.add(newValue[0])
                                } else {
                                    newPin[index] = newValue[0]
                                }
                            } else if (index < newPin.size) {
                                newPin.removeAt(index)
                            }
                            pin = newPin.joinToString("")
                        }
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    visualTransformation = if (showPin) androidx.compose.ui.text.input.VisualTransformation.None
                    else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonOrange,
                        unfocusedBorderColor = TextTertiary.copy(alpha = 0.3f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showPin) "Hide PIN" else "Show PIN",
                fontSize = 12.sp,
                color = NeonOrange,
                modifier = Modifier.clickable { showPin = !showPin }
            )

            Text(
                text = "Forgot PIN?",
                fontSize = 12.sp,
                color = NeonOrange,
                modifier = Modifier.clickable { }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PremiumButton(
            text = "Confirm Payment",
            onClick = { onPinSubmit(pin) },
            enabled = pin.length == 4,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun PaymentConfirmationStep(
    amount: String,
    recipientName: String,
    remarks: String,
    onNewTransaction: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Success Animation
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(color = SuccessGreen.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = SuccessGreen,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Payment Successful!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )

        Text(
            text = "₹${amount}",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = SuccessGreen,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "sent to $recipientName",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Payment Details",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Amount", fontSize = 12.sp, color = TextSecondary)
                    Text("₹$amount", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }

                if (remarks.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Remark", fontSize = 12.sp, color = TextSecondary)
                        Text(remarks, fontSize = 12.sp, color = TextPrimary)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = TextTertiary.copy(alpha = 0.2f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Date & Time", fontSize = 12.sp, color = TextSecondary)
                    Text(
                        java.text.SimpleDateFormat("dd MMM, HH:mm", java.util.Locale.getDefault()).format(
                            java.util.Date()
                        ),
                        fontSize = 12.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PremiumButton(
                text = "Make Another Payment",
                onClick = onNewTransaction
            )

            PremiumButton(
                text = "Done",
                onClick = onClose,
                backgroundColor = DarkCard,
                textColor = NeonOrange
            )
        }
    }
}
