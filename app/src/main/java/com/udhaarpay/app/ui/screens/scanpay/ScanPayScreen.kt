package com.udhaarpay.app.ui.screens.scanpay

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.components.UdhaarPayTextButton
import com.udhaarpay.app.ui.screens.nfc.NFCPaymentScreen
import com.udhaarpay.app.ui.viewmodel.NFCPaymentViewModel
import com.udhaarpay.app.ui.viewmodel.PaymentContact
import com.udhaarpay.app.ui.viewmodel.UPIPaymentViewModel
import java.io.InputStream

private enum class ScanPayMode(val label: String) {
    Scan("Scan QR"),
    Contacts("Contacts"),
    Account("Account No"),
    Self("Self Transfer")
}

@Composable
fun ScanPayScreen(
    onNavigateHome: () -> Unit = {},
    upiPaymentViewModel: UPIPaymentViewModel = hiltViewModel()
) {
    val contacts by upiPaymentViewModel.contacts.collectAsState()
    val accounts by upiPaymentViewModel.bankAccounts.collectAsState()
    val cards by upiPaymentViewModel.creditCards.collectAsState()
    val wallet by upiPaymentViewModel.walletAccount.collectAsState()
    val currentUser by upiPaymentViewModel.currentUser.collectAsState()
    val statusMessage by upiPaymentViewModel.statusMessage.collectAsState()

    var selectedMode by remember { mutableStateOf(ScanPayMode.Scan) }
    var scannedResult by remember { mutableStateOf<String?>(null) }
    var selectedContact by remember { mutableStateOf<PaymentContact?>(null) }
    var accountNumber by remember { mutableStateOf("") }
    var ifsc by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Miscellaneous") }
    var sourceType by remember { mutableStateOf("bank") }
    var sourceId by remember { mutableStateOf<Long?>(null) }
    var enteredPin by remember { mutableStateOf("") }
    var walletLimitInput by remember { mutableStateOf((currentUser?.walletPinFreeLimit ?: 200.0).toString()) }
    var showNfcDialog by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val nfcViewModel: NFCPaymentViewModel = hiltViewModel()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(sourceType, accounts, cards, wallet) {
        sourceId = when (sourceType) {
            "bank" -> accounts.firstOrNull { !it.accountType.equals("Wallet", true) }?.accountId
            "card" -> cards.firstOrNull()?.cardId
            else -> wallet?.accountId
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            inputStream?.use { stream ->
                val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                scannedResult = decodeQrFromBitmap(bitmap)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        scannedResult = decodeQrFromBitmap(bitmap)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Scan & Pay", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Smart UPI payments with local security", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            IconButton(onClick = { showMessageDialog = true }) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Message contacts")
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            ScanPayMode.entries.forEach { mode ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedMode = mode },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedMode == mode) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Text(
                        mode.label,
                        modifier = Modifier.padding(10.dp),
                        fontSize = 12.sp,
                        fontWeight = if (selectedMode == mode) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        when (selectedMode) {
            ScanPayMode.Scan -> {
                AnimatedScanButton(onScan = { cameraLauncher.launch(null) })
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    UdhaarPayButton(
                        text = "Scan QR from Camera",
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.weight(1f)
                    )
                    UdhaarPayButton(
                        text = "Scan from Gallery",
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (!scannedResult.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Scanned: $scannedResult", color = MaterialTheme.colorScheme.secondary)
                }
            }

            ScanPayMode.Contacts -> {
                Text("Pay from Contacts", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                val contactScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(contactScroll)
                            .padding(bottom = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        contacts.forEach { contact ->
                            Card(
                                onClick = { selectedContact = contact },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedContact?.upiId == contact.upiId) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Contacts, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(contact.name, fontWeight = FontWeight.SemiBold)
                                        Text("${contact.phone} | ${contact.upiId}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (selectedContact?.upiId == contact.upiId) {
                                        Text("Selected", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ScanPayMode.Account -> {
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it.filter(Char::isDigit) },
                    label = { Text("Recipient Account Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = ifsc,
                    onValueChange = { ifsc = it.uppercase() },
                    label = { Text("IFSC Code") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ScanPayMode.Self -> {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("Self Transfer", fontWeight = FontWeight.SemiBold)
                        Text("Move money from Bank/Card to Wallet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note / Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category (Transport, Salary, Beauty, Books, Shopping, Eats...)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        Text("Pay Using", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            SourceChoice(
                title = "Bank",
                icon = Icons.Default.AccountBalance,
                selected = sourceType == "bank",
                onSelect = { sourceType = "bank" },
                modifier = Modifier.weight(1f)
            )
            SourceChoice(
                title = "Card",
                icon = Icons.Default.Contactless,
                selected = sourceType == "card",
                onSelect = { sourceType = "card" },
                modifier = Modifier.weight(1f)
            )
            SourceChoice(
                title = "Wallet",
                icon = Icons.Default.Savings,
                selected = sourceType == "wallet",
                onSelect = { sourceType = "wallet" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(6.dp))
        val sourceOptions = when (sourceType) {
            "bank" -> accounts.filter { !it.accountType.equals("Wallet", true) }
                .map { it.accountId to "${it.bankName} ${it.accountNumber}" }

            "card" -> cards.map { it.cardId to "${it.issuer} ****${it.cardNumber} (${it.cardType})" }
            else -> listOfNotNull(wallet?.let { it.accountId to "${it.bankName} ${it.accountNumber}" })
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            sourceOptions.take(3).forEach { pair ->
                Card(
                    onClick = { sourceId = pair.first },
                    colors = CardDefaults.cardColors(
                        containerColor = if (sourceId == pair.first) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(pair.second, modifier = Modifier.padding(8.dp), fontSize = 11.sp)
                }
            }
            if (sourceOptions.isEmpty()) {
                Text(
                    text = "No source available",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = walletLimitInput,
                onValueChange = { walletLimitInput = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Wallet no-PIN limit") },
                modifier = Modifier.weight(1f)
            )
            UdhaarPayButton(
                text = "Set Limit",
                onClick = {
                    walletLimitInput.toDoubleOrNull()?.let { upiPaymentViewModel.setWalletPinFreeLimit(it) }
                },
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            UdhaarPayButton(
                text = if (selectedMode == ScanPayMode.Self) "Transfer to Self Wallet" else "Pay Now",
                onClick = {
                    val amountValue = amountText.toDoubleOrNull() ?: 0.0
                    if (amountValue <= 0.0) return@UdhaarPayButton
                    val walletNoPinLimit = currentUser?.walletPinFreeLimit ?: 200.0

                    val action: () -> Unit = {
                        when (selectedMode) {
                            ScanPayMode.Scan -> {
                                upiPaymentViewModel.payToUpi(
                                    recipientUpi = scannedResult.orEmpty(),
                                    amount = amountValue,
                                    sourceType = sourceType,
                                    sourceId = sourceId,
                                    category = category,
                                    note = note,
                                    enteredPin = enteredPin
                                )
                            }

                            ScanPayMode.Contacts -> {
                                selectedContact?.let {
                                    upiPaymentViewModel.payFromContact(
                                        contact = it,
                                        amount = amountValue,
                                        sourceType = sourceType,
                                        sourceId = sourceId,
                                        category = category,
                                        note = note,
                                        enteredPin = enteredPin
                                    )
                                }
                            }

                            ScanPayMode.Account -> {
                                upiPaymentViewModel.payToAccountNumber(
                                    accountNumber = accountNumber,
                                    ifsc = ifsc,
                                    amount = amountValue,
                                    sourceType = sourceType,
                                    sourceId = sourceId,
                                    category = category,
                                    note = note,
                                    enteredPin = enteredPin
                                )
                            }

                            ScanPayMode.Self -> {
                                upiPaymentViewModel.transferToSelfWallet(
                                    amount = amountValue,
                                    sourceType = sourceType,
                                    sourceId = sourceId,
                                    note = note,
                                    enteredPin = enteredPin
                                )
                            }
                        }
                        Unit
                    }
                    pendingAction = action
                    val needsPin = when (sourceType) {
                        "wallet" -> amountValue > walletNoPinLimit
                        else -> true
                    }
                    if (needsPin) {
                        showPinDialog = true
                    } else {
                        enteredPin = ""
                        pendingAction?.invoke()
                        pendingAction = null
                    }
                },
                modifier = Modifier.weight(1f)
            )
            UdhaarPayButton(
                text = "NFC",
                onClick = { showNfcDialog = true },
                modifier = Modifier.width(96.dp)
            )
        }

        if (!statusMessage.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(statusMessage ?: "", color = MaterialTheme.colorScheme.primary)
            UdhaarPayTextButton(text = "Dismiss", onClick = { upiPaymentViewModel.clearStatus() })
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Enter UPI PIN") },
            text = {
                OutlinedTextField(
                    value = enteredPin,
                    onValueChange = { enteredPin = it.filter(Char::isDigit).take(6) },
                    label = { Text("UPI PIN") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPinDialog = false
                        pendingAction?.invoke()
                        pendingAction = null
                    }
                ) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showMessageDialog) {
        MessageContactsDialog(
            contacts = contacts,
            onDismiss = { showMessageDialog = false },
            onPay = { contact ->
                selectedMode = ScanPayMode.Contacts
                selectedContact = contact
                showMessageDialog = false
            }
        )
    }

    if (showNfcDialog) {
        Dialog(onDismissRequest = { showNfcDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                Box(Modifier.size(360.dp, 560.dp)) {
                    NFCPaymentScreen(
                        onBack = { showNfcDialog = false },
                        onReturnHomeAfterSuccess = {
                            showNfcDialog = false
                            onNavigateHome()
                        },
                        nfcViewModel = nfcViewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceChoice(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AnimatedScanButton(onScan: () -> Unit) {
    val pulse = rememberInfiniteTransition(label = "scanPulse")
    val outerScale by pulse.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "scanOuter"
    )
    val midScale by pulse.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "scanMid"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size((150 * outerScale).dp)) {
            drawCircle(color = Color(0x22FF3B47))
        }
        Canvas(modifier = Modifier.size((120 * midScale).dp)) {
            drawCircle(color = Color(0x33FF3B47))
        }
        Card(
            modifier = Modifier
                .size(110.dp)
                .clickable { onScan() },
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.QrCode, contentDescription = "Scan", tint = Color.White, modifier = Modifier.size(30.dp))
                Spacer(Modifier.height(2.dp))
                Text("SCAN", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MessageContactsDialog(
    contacts: List<PaymentContact>,
    onDismiss: () -> Unit,
    onPay: (PaymentContact) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val contactScroll = rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Contacts & Message") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    label = { Text("Message (mock)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Column(
                    modifier = Modifier
                        .height(180.dp)
                        .verticalScroll(contactScroll)
                ) {
                    contacts.forEach { contact ->
                        Card(
                            onClick = { onPay(contact) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(contact.name, fontWeight = FontWeight.SemiBold)
                                    Text(contact.phone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("Pay")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

fun decodeQrFromBitmap(bitmap: Bitmap?): String {
    if (bitmap == null) return ""
    return try {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val source = RGBLuminanceSource(width, height, pixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val result = MultiFormatReader().decode(binaryBitmap)
        result.text ?: ""
    } catch (_: Exception) {
        ""
    }
}

