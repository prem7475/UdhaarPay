package com.udhaarpay.app.ui.screens.nfc

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.local.entities.CreditCard
import com.udhaarpay.app.ui.viewmodel.NFCPaymentViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun NFCPaymentScreen(
    onBack: () -> Unit = {},
    onReturnHomeAfterSuccess: (() -> Unit)? = null,
    nfcViewModel: NFCPaymentViewModel = hiltViewModel()
) {
    val cards by nfcViewModel.creditCards.collectAsState()
    val selectedCard by nfcViewModel.selectedCard.collectAsState()
    val paymentAmount by nfcViewModel.paymentAmount.collectAsState()
    val nfcStatus by nfcViewModel.nfcStatus.collectAsState()
    val lastTransaction by nfcViewModel.lastTransaction.collectAsState()
    val rewardAmount by nfcViewModel.rewardAmount.collectAsState()

    var showSuccess by remember { mutableStateOf(false) }
    var showRewardFlight by remember { mutableStateOf(false) }
    var animatingAmount by remember { mutableStateOf(0) }
    var awaitingTap by remember { mutableStateOf(false) }

    val paymentValue = paymentAmount.toDoubleOrNull() ?: 0.0
    val targetBalance = ((selectedCard?.balance ?: 0.0) - paymentValue).coerceAtLeast(0.0).toInt()
    val animatedBalance by animateIntAsState(
        targetValue = if (nfcStatus == "Processing" || nfcStatus == "Scanning") targetBalance else (selectedCard?.balance
            ?: 0.0).toInt(),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "balance_countdown"
    )

    LaunchedEffect(nfcStatus) {
        if (nfcStatus == "Processing" || nfcStatus == "Scanning") {
            showSuccess = false
            showRewardFlight = false
            animatingAmount = paymentValue.toInt()
            awaitingTap = false
        }
        if (nfcStatus == "Success") {
            showSuccess = true
            showRewardFlight = true
            if (onReturnHomeAfterSuccess != null) {
                delay(2300)
                onReturnHomeAfterSuccess()
            }
            delay(1600)
            showRewardFlight = false
        }
    }

    LaunchedEffect(selectedCard?.cardId, paymentValue, nfcStatus) {
        if (nfcStatus !in listOf("Processing", "Scanning") && selectedCard != null && paymentValue > 0.0) {
            awaitingTap = true
        }
        if (paymentValue <= 0.0) {
            awaitingTap = false
        }
    }

    BackHandler {
        if (nfcStatus == "Success" && onReturnHomeAfterSuccess != null) {
            onReturnHomeAfterSuccess()
        } else {
            onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (nfcStatus == "Success" && onReturnHomeAfterSuccess != null) {
                            onReturnHomeAfterSuccess()
                        } else {
                            onBack()
                        }
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    "Tap To Pay",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Icon(
                    Icons.Default.Contactless,
                    contentDescription = "NFC",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(14.dp))

            SlidingCardSelector(
                cards = cards,
                selectedCard = selectedCard,
                awaitingTap = awaitingTap,
                onCardSelected = { card ->
                    nfcViewModel.selectCard(card)
                    nfcViewModel.resetReadyState()
                    if (paymentValue > 0.0) awaitingTap = true
                },
                onCardTappedForPayment = { card ->
                    if (selectedCard?.cardId == card.cardId && awaitingTap) {
                        nfcViewModel.processNFCPayment(paymentAmount)
                    }
                }
            )

            Spacer(Modifier.height(14.dp))

            selectedCard?.let { card ->
                SelectedCardInfo(
                    card = card,
                    displayBalance = animatedBalance
                )
            }

            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = paymentAmount,
                onValueChange = { nfcViewModel.setPaymentAmount(it.filter { c -> c.isDigit() || c == '.' }) },
                label = { Text("Enter Amount") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.75f)
            )
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    awaitingTap = true
                    nfcViewModel.resetReadyState()
                },
                enabled = selectedCard != null && paymentValue > 0.0 && nfcStatus !in listOf("Processing", "Scanning"),
                modifier = Modifier.fillMaxWidth(0.7f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Ready To Tap")
            }

            Spacer(Modifier.height(10.dp))
            Text(
                when {
                    nfcStatus in listOf("Processing", "Scanning") -> nfcStatus
                    awaitingTap -> "Tap the selected card to complete payment"
                    nfcStatus == "Ready" -> "Select card and start NFC tap"
                    else -> nfcStatus
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (nfcStatus in listOf("Scanning", "Processing")) {
            NfcProcessingOverlay(animatingAmount = animatingAmount)
        }

        if (showSuccess && lastTransaction != null) {
            SuccessOverlay(
                amount = lastTransaction?.amount ?: 0.0,
                reward = rewardAmount,
                showRewardFlight = showRewardFlight,
                cardLast4 = selectedCard?.cardNumber?.takeLast(4).orEmpty()
            )
        }
    }
}

@Composable
private fun SlidingCardSelector(
    cards: List<CreditCard>,
    selectedCard: CreditCard?,
    awaitingTap: Boolean,
    onCardSelected: (CreditCard) -> Unit,
    onCardTappedForPayment: (CreditCard) -> Unit
) {
    if (cards.isEmpty()) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Text("No cards available", fontWeight = FontWeight.SemiBold)
                Text(
                    "Add a RuPay card from Cards section to use NFC",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
        return
    }

    var expanded by remember { mutableStateOf(false) }
    val selectedId = selectedCard?.cardId ?: cards.first().cardId
    val selectedIndex = cards.indexOfFirst { it.cardId == selectedId }.coerceAtLeast(0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        if (awaitingTap) {
            WifiTapRings()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clickable { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            cards.forEachIndexed { index, card ->
                val isSelected = card.cardId == selectedId
                val relative = index - selectedIndex

                val targetX = if (expanded) (relative * 66).dp else 0.dp
                val targetY = if (expanded) {
                    if (isSelected) (-10).dp else 22.dp
                } else {
                    (index * 10).dp
                }
                val targetRotation = if (expanded) {
                    (-relative * 6f).coerceIn(-18f, 18f)
                } else {
                    if (index % 2 == 0) -3.5f else 3.5f
                }
                val targetScale = if (expanded) {
                    if (isSelected) 1f else 0.86f
                } else {
                    (0.92f + index * 0.02f).coerceAtMost(0.98f)
                }
                val targetAlpha = if (expanded && !isSelected) 0.58f else 1f

                val animX by animateDpAsState(targetValue = targetX, animationSpec = tween(420), label = "deck_x_$index")
                val animY by animateDpAsState(targetValue = targetY, animationSpec = tween(420), label = "deck_y_$index")
                val animScale by animateFloatAsState(targetValue = targetScale, animationSpec = tween(420), label = "deck_scale_$index")
                val animRot by animateFloatAsState(targetValue = targetRotation, animationSpec = tween(420), label = "deck_rot_$index")
                val animAlpha by animateFloatAsState(targetValue = targetAlpha, animationSpec = tween(420), label = "deck_alpha_$index")
                val elevation by animateFloatAsState(
                    targetValue = if (isSelected && expanded) 36f else 14f,
                    animationSpec = tween(360),
                    label = "deck_elevation_$index"
                )

                PremiumCard(
                    card = card,
                    modifier = Modifier
                        .offset(x = animX, y = animY)
                        .zIndex(if (isSelected) 100f else index.toFloat())
                        .graphicsLayer {
                            scaleX = animScale
                            scaleY = animScale
                            rotationZ = animRot
                            rotationX = if (expanded) 4f else 7f
                            alpha = animAlpha
                            shadowElevation = elevation
                            cameraDistance = 22f * density
                        }
                        .clickable {
                            if (!expanded) {
                                expanded = true
                                onCardSelected(card)
                            } else if (isSelected) {
                                if (awaitingTap) onCardTappedForPayment(card)
                            } else {
                                onCardSelected(card)
                            }
                        }
                )
            }
        }

        Text(
            text = when {
                !expanded -> "Tap deck to open cards"
                awaitingTap -> "Tap selected card to pay"
                else -> "Tap any card to select"
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun WifiTapRings() {
    val transition = rememberInfiniteTransition(label = "tap_rings")
    val r1 by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(animation = tween(700), repeatMode = RepeatMode.Reverse),
        label = "ring1"
    )
    val r2 by transition.animateFloat(
        initialValue = 0.68f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(animation = tween(900), repeatMode = RepeatMode.Reverse),
        label = "ring2"
    )
    val ringColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)

    Canvas(modifier = Modifier.size(300.dp)) {
        val base = size.minDimension / 2.7f
        drawCircle(
            color = ringColor,
            radius = base * r1,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
        )
        drawCircle(
            color = ringColor.copy(alpha = 0.22f),
            radius = base * r2,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
        )
        drawCircle(
            color = ringColor.copy(alpha = 0.16f),
            radius = base * (1.05f + (r2 - 0.68f)),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )
    }
}

@Composable
private fun PremiumCard(
    card: CreditCard,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(width = 230.dp, height = 145.dp)
            .clip(RoundedCornerShape(22.dp)),
        color = Color.Transparent,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 12.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("RuPay", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "**** **** **** ${card.cardNumber.takeLast(4)}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(12.dp))
                Text(card.issuer, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SelectedCardInfo(
    card: CreditCard,
    displayBalance: Int
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.86f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Selected Card", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text("Available Balance: INR $displayBalance", color = MaterialTheme.colorScheme.onSurface)
            Text("Last 4 Digits: ${card.cardNumber.takeLast(4)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Expiry: ${card.expiry}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Card Type: ${card.cardType}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "NFC Brand: ${if (card.cardType.equals("rupay", true)) "RuPay" else "Not Supported"}",
                color = if (card.cardType.equals("rupay", true)) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun NfcProcessingOverlay(animatingAmount: Int) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val transition = rememberInfiniteTransition(label = "nfc_transition")
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val ripple by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple"
    )

    val moneyProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        moneyProgress.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.52f))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(240.dp).blur(2.dp)) {
                val base = size.minDimension / 2f
                drawCircle(
                    color = primary.copy(alpha = 0.26f),
                    radius = base * pulse
                )
                drawCircle(
                    color = secondary.copy(alpha = 0.28f * (1f - ripple)),
                    radius = base * (0.6f + ripple)
                )
                drawCircle(
                    color = primary.copy(alpha = 0.18f * (1f - ripple)),
                    radius = base * (0.85f + ripple * 0.8f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f)
                )
            }

            Icon(
                Icons.Default.Contactless,
                contentDescription = null,
                tint = onSurface,
                modifier = Modifier.size(56.dp)
            )
            Text(
                "INR $animatingAmount",
                color = onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(y = (-96).dp)
            )
            Icon(
                Icons.Default.CurrencyRupee,
                contentDescription = null,
                tint = secondary,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (moneyProgress.value * 130f).roundToInt(),
                            y = (-moneyProgress.value * 80f).roundToInt()
                        )
                    }
                    .size(24.dp)
            )
            Icon(
                Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = onSurface,
                modifier = Modifier.offset(x = 134.dp, y = (-82).dp)
            )
        }
    }
}

@Composable
private fun SuccessOverlay(
    amount: Double,
    reward: Double,
    showRewardFlight: Boolean,
    cardLast4: String
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val checkScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(380),
        label = "check_scale"
    )
    val confettiProgress = remember { Animatable(0f) }
    LaunchedEffect(amount) {
        confettiProgress.snapTo(0f)
        confettiProgress.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(26) { idx ->
                val x = (size.width / 28f) * (idx + 1)
                val y = (confettiProgress.value * size.height * 0.65f) + Random(idx).nextInt(-40, 60)
                drawCircle(
                    color = if (idx % 2 == 0) secondary else primary,
                    radius = Random(idx + 99).nextInt(2, 4).toFloat(),
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF22C55E),
                modifier = Modifier
                    .size(82.dp)
                    .graphicsLayer {
                        scaleX = checkScale
                        scaleY = checkScale
                    }
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "INR ${"%.2f".format(amount)} Paid Successfully",
                color = onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Card ****$cardLast4 updated.",
                color = secondary,
                fontWeight = FontWeight.SemiBold
            )
            AnimatedVisibility(visible = showRewardFlight) {
                Row(
                    modifier = Modifier
                        .offset(x = 0.dp, y = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Text(
                        "Cashback +INR ${"%.2f".format(reward)}",
                        color = secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Wallet, contentDescription = null, tint = primary)
                }
            }
        }
    }
}
