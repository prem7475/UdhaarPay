package com.udhaarpay.app.ui.screens.nfc

import android.nfc.NfcAdapter
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.model.CreditCard
import com.udhaarpay.app.ui.viewmodel.NFCViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NFCPaymentScreen(
    onBack: () -> Unit,
    viewModel: NFCViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val cards by viewModel.cards.collectAsState()
    val selectedCard by viewModel.selectedCard.collectAsState()
    // ...existing code...
}