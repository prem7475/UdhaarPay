package com.example.udhaarpay.ui.screens.nfc

import android.content.Context
import android.nfc.NfcAdapter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.data.model.BankCard
import com.example.udhaarpay.ui.viewmodel.NFCViewModel

@Composable
fun NFCPaymentScreen(
    onBack: () -> Unit,
    viewModel: NFCViewModel = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val nfcAvailable = remember { isNfcAvailable(context) }
    var status by remember { mutableStateOf("Tap your NFC-enabled card or phone to pay") }
    val selectedCard by viewModel.selectedCard.collectAsState(initial = null as BankCard?)

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("NFC Payments", style = MaterialTheme.typography.headlineSmall)

            if (!nfcAvailable) {
                Text("NFC not available on this device or it's disabled. Please enable NFC in settings.")
            }

            // Card preview (simple)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (selectedCard != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${selectedCard!!.bankName}", color = Color.White, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("•••• •••• •••• ${selectedCard!!.lastFourDigits}", color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Icon(imageVector = Icons.Default.Contactless, contentDescription = "NFC", tint = Color.White)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No card selected", color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Add a card in Wallet to enable contactless payments", color = Color.LightGray)
                        }
                    }
                }
            }

            // Tap area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF222222))
                    .clickable {
                        // Simulate tap
                        if (nfcAvailable) status = "Payment successful — ₹499 debited"
                        else status = "NFC not available"
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Contactless, contentDescription = "Tap", tint = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(status, color = Color.White)
                }
            }

            Button(onClick = { /* Placeholder: open NFC settings or start real NFC flow */ }) {
                Text("Simulate Tap")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Back
            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}

private fun isNfcAvailable(context: Context): Boolean {
    return try {
        val adapter = NfcAdapter.getDefaultAdapter(context)
        adapter != null && adapter.isEnabled
    } catch (e: Exception) {
        false
    }
}
