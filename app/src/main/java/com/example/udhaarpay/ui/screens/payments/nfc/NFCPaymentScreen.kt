
package com.example.udhaarpay.ui.screens.payments.nfc

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Mock credit card data
val mockCards = listOf(
    CreditCard("1234", "RuPay", "HDFC", 5000.0, "12/25"),
    CreditCard("5678", "RuPay", "ICICI", 3500.0, "11/26"),
    CreditCard("9012", "RuPay", "Axis", 4200.0, "03/27")
)

data class CreditCard(val last4: String, val type: String, val issuer: String, val balance: Double, val expiry: String)

@Composable
fun NFCPaymentScreen() {
    val density = LocalDensity.current
    var cards by remember { mutableStateOf(mockCards) }
    var selectedIndex by remember { mutableStateOf(0) }
    var isExpanded by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var showNfcAnim by remember { mutableStateOf(false) }
    val nfcAnim = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("NFC Payment", fontSize = 22.sp, color = Color(0xFF2563EB))
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
            AnimatedCardStack(
                cards = cards,
                selectedIndex = selectedIndex,
                isExpanded = isExpanded,
                onCardClick = { isExpanded = !isExpanded },
                onCardSelected = { idx ->
                    selectedIndex = idx
                    isExpanded = false
                },
                density = density
            )
        }
        Spacer(Modifier.height(16.dp))
        val selectedCard = cards[selectedIndex]
        Text("Selected Card: ${selectedCard.issuer} ••••${selectedCard.last4}", color = Color(0xFF059669))
        Text("Balance: ₹${selectedCard.balance}", color = Color(0xFF059669))
        Text("Expiry: ${selectedCard.expiry}", color = Color(0xFF7C3AED))
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                showNfcAnim = true
                coroutineScope.launch {
                    nfcAnim.snapTo(1f)
                    nfcAnim.animateTo(1.15f, animationSpec = tween(300))
                    nfcAnim.animateTo(1f, animationSpec = tween(300))
                    showSuccess = true
                    showNfcAnim = false
                }
            },
            enabled = amount.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tap to Pay")
        }
        if (showNfcAnim) {
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier
                    .size(60.dp)
                    .graphicsLayer {
                        scaleX = nfcAnim.value
                        scaleY = nfcAnim.value
                    }
                    .background(Color(0xFF2563EB), shape = RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = Icons.Filled.Payment,
                    contentDescription = "NFC",
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        if (showSuccess) {
            AlertDialog(
                onDismissRequest = { showSuccess = false },
                title = { Text("Payment Successful") },
                text = { Text("Paid ₹$amount using card ••••${selectedCard.last4}") },
                confirmButton = {
                    TextButton(onClick = { showSuccess = false }) { Text("OK") }
                }
            )
        }
    }
}

@Composable
fun AnimatedCardStack(
    cards: List<CreditCard>,
    selectedIndex: Int,
    isExpanded: Boolean,
    onCardClick: () -> Unit,
    onCardSelected: (Int) -> Unit,
    density: androidx.compose.ui.unit.Density
) {
    val transitionDur = 400
    val cardCount = cards.size
    val baseZ = 10f
    val baseOffset = 18f // dp as float
    val fanAngle = 18f
    val fanSpread = 60f // dp as float
    val animProgress = remember { Animatable(if (isExpanded) 1f else 0f) }
    LaunchedEffect(isExpanded) {
        animProgress.animateTo(if (isExpanded) 1f else 0f, animationSpec = tween(durationMillis = transitionDur))
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (!isExpanded) {
            // Animated stack view
            cards.forEachIndexed { idx, card ->
                val offset = baseOffset * (cardCount - idx - 1) * (1 - animProgress.value)
                val offsetDp = Dp(offset)
                val scale = 1f - 0.04f * (cardCount - idx - 1) * (1 - animProgress.value)
                val z = baseZ + idx
                Card(
                    modifier = Modifier
                        .offset(y = offsetDp)
                        .size(width = 220.dp, height = 120.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationY = with(density) { offsetDp.toPx() }
                            shadowElevation = z
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .clickable { onCardClick() },
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("••••${card.last4}", fontSize = 20.sp, color = Color(0xFF2563EB))
                    }
                }
            }
        } else {
            // Animated fan out view
            cards.forEachIndexed { idx, card ->
                val angle = (idx - (cardCount - 1) / 2f) * fanAngle * animProgress.value
                val xOffset = (idx - (cardCount - 1) / 2f) * fanSpread * animProgress.value
                val xOffsetDp = Dp(xOffset)
                val scale = 0.9f + 0.1f * animProgress.value
                Card(
                    modifier = Modifier
                        .graphicsLayer {
                            rotationZ = angle
                            translationX = with(density) { xOffsetDp.toPx() }
                            scaleX = scale
                            scaleY = scale
                            shadowElevation = baseZ + idx
                        }
                        .size(width = 140.dp, height = 90.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .clickable { onCardSelected(idx) },
                    colors = CardDefaults.cardColors(containerColor = if (idx == selectedIndex) Color(0xFFDCFCE7) else Color.White)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("••••${card.last4}", fontSize = 16.sp, color = Color(0xFF2563EB))
                    }
                }
            }
        }
    }
}
