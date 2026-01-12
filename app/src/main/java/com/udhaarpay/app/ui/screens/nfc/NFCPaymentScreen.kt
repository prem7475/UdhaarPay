package com.udhaarpay.app.ui.screens.nfc

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.graphicsLayer
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.R
import com.udhaarpay.app.data.local.entities.CreditCard
import com.udhaarpay.app.ui.viewmodel.NFCPaymentViewModel
import com.udhaarpay.app.ui.viewmodel.NFCTransaction
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NFCPaymentScreen(
    onBack: () -> Unit = {},
    nfcViewModel: NFCPaymentViewModel = hiltViewModel()
) {
    val cards by nfcViewModel.creditCards.collectAsState()
    val selectedCard by nfcViewModel.selectedCard.collectAsState()
    val paymentAmount by nfcViewModel.paymentAmount.collectAsState()
    val isCardExpanded by nfcViewModel.isCardExpanded.collectAsState()
    val nfcStatus by nfcViewModel.nfcStatus.collectAsState()
    val lastTransaction by nfcViewModel.lastTransaction.collectAsState()
    var showReward by remember { mutableStateOf(false) }

    LaunchedEffect(nfcStatus) {
        if (nfcStatus == "Success") {
            showReward = true
            delay(1800)
            showReward = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF6366F1))
                )
            )
            .padding(18.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(18.dp))
            Text("NFC Payment", fontWeight = FontWeight.Bold, fontSize = 26.sp, color = Color.White)
            Spacer(Modifier.height(18.dp))
            AnimatedVisibility(visible = !isCardExpanded, enter = fadeIn(), exit = fadeOut()) {
                CardStack(cards, onExpand = { nfcViewModel.expandCardStack() })
            }
            AnimatedVisibility(visible = isCardExpanded, enter = fadeIn(), exit = fadeOut()) {
                CardSelection(cards, selectedCard, onSelect = {
                    nfcViewModel.selectCard(it)
                    nfcViewModel.collapseCardStack()
                })
            }
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = paymentAmount,
                onValueChange = { nfcViewModel.setPaymentAmount(it) },
                label = { Text("Enter Amount") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = {
                    if (selectedCard != null && paymentAmount.isNotBlank()) {
                        nfcViewModel.processNFCPayment(paymentAmount)
                    }
                },
                enabled = selectedCard != null && paymentAmount.isNotBlank() && nfcStatus != "Processing",
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(54.dp).width(180.dp)
            ) {
                if (nfcStatus == "Processing") {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (nfcStatus == "Processing") "Paying..." else "Pay via NFC", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(24.dp))
            AnimatedContent(targetState = nfcStatus, transitionSpec = {
                fadeIn(animationSpec = tween(400)) with fadeOut(animationSpec = tween(400))
            }) { status ->
                if (status == "Success" && lastTransaction != null) {
                    PaymentSuccessView(lastTransaction!!, showReward)
                } else if (status != "Ready") {
                    Text(status, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun CardStack(cards: List<CreditCard>, onExpand: () -> Unit) {
    Box(Modifier.height(180.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
        cards.take(3).reversed().forEachIndexed { i, card ->
            val offset = 16.dp * i
                val scale = 1f - 0.07f * i
                CardView(
                card = card,
                modifier = Modifier
                    .offset(y = offset)
                        .graphicsLayer { this.scaleX = scale; this.scaleY = scale }
                    .clickable { onExpand() },
                showDetails = i == 0
            )
        }
    }
}

@Composable
fun CardSelection(cards: List<CreditCard>, selected: CreditCard?, onSelect: (CreditCard) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Select Card", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            cards.forEach { card ->
                CardView(
                    card = card,
                    modifier = Modifier.clickable { onSelect(card) },
                        showDetails = selected?.cardId == card.cardId
                )
            }
        }
    }
}

@Composable
fun CardView(card: CreditCard, modifier: Modifier = Modifier, showDetails: Boolean = false) {
    val cardColors = listOf(Color(0xFF6366F1), Color(0xFF0EA5E9), Color(0xFF22C55E))
        val color = cardColors[(card.cardId.toInt() - 1) % cardColors.size]
    Surface(
        modifier = modifier
            .size(width = 220.dp, height = 140.dp)
            .clip(RoundedCornerShape(22.dp)),
        color = color,
        shadowElevation = 12.dp
    ) {
        Column(Modifier.padding(18.dp)) {
            Text("RuPay", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
                Text("**** **** **** ${card.cardNumber.takeLast(4)}", color = Color.White, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            if (showDetails) {
                Text("Balance: ₹${card.balance}", color = Color.White, fontWeight = FontWeight.SemiBold)
                Text("Expiry: ${card.expiry}", color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun PaymentSuccessView(txn: NFCTransaction, showReward: Boolean) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Payment Successful!", color = Color(0xFF22C55E), fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(Modifier.height(8.dp))
            Text("Paid ₹${txn.amount} to ${txn.merchant}", color = Color.White, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text("Txn ID: ${txn.id}", color = Color(0xFFCBD5E1), fontSize = 13.sp)
            AnimatedVisibility(visible = showReward, enter = slideInVertically() + fadeIn(), exit = fadeOut()) {
                Text("🎁 Reward Unlocked!", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}