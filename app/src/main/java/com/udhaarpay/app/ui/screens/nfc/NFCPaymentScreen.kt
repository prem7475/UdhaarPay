package com.udhaarpay.app.ui.screens.nfc

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.local.entities.CreditCard
import com.udhaarpay.app.ui.components.PremiumInfoCard
import com.udhaarpay.app.ui.components.PremiumPill
import com.udhaarpay.app.ui.components.PremiumScreen
import com.udhaarpay.app.ui.components.PremiumSectionHeader
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.theme.UdhaarPayBrushes
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
    var showTapPrompt by remember { mutableStateOf(true) }
    var awaitingTap by remember { mutableStateOf(false) }
    var selectedCardExpanded by remember { mutableStateOf(false) }

    val paymentValue = paymentAmount.toDoubleOrNull() ?: 0.0
    val displayBalanceTarget = ((selectedCard?.balance ?: 0.0) - paymentValue).coerceAtLeast(0.0).toInt()
    val animatedBalance by animateIntAsState(
        targetValue = if (nfcStatus == "Scanning" || nfcStatus == "Processing") displayBalanceTarget else (selectedCard?.balance
            ?: 0.0).toInt(),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "balance_countdown"
    )
    val deckBlur by animateDpAsState(
        targetValue = if (selectedCard != null && awaitingTap) 10.dp else 0.dp,
        animationSpec = tween(360),
        label = "deck_blur"
    )
    LaunchedEffect(nfcStatus) {
        when (nfcStatus) {
            "Scanning", "Processing" -> {
                showSuccess = false
                showRewardFlight = false
                showTapPrompt = false
                awaitingTap = false
                selectedCardExpanded = true
            }
            "Success" -> {
                showSuccess = true
                showRewardFlight = true
                selectedCardExpanded = true
                delay(1750)
                showRewardFlight = false
                if (onReturnHomeAfterSuccess != null) {
                    delay(450)
                    onReturnHomeAfterSuccess()
                }
            }
            else -> {
                showTapPrompt = true
            }
        }
    }

    LaunchedEffect(selectedCard?.cardId, paymentValue, nfcStatus) {
        awaitingTap = selectedCard != null && paymentValue > 0.0 && nfcStatus !in listOf("Scanning", "Processing", "Success")
    }

    BackHandler {
        if (nfcStatus == "Success" && onReturnHomeAfterSuccess != null) {
            onReturnHomeAfterSuccess()
        } else {
            onBack()
        }
    }

    PremiumScreen {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TopBar(
                    title = "Tap To Pay",
                    onBack = {
                        if (nfcStatus == "Success" && onReturnHomeAfterSuccess != null) {
                            onReturnHomeAfterSuccess()
                        } else {
                            onBack()
                        }
                    }
                )

                PremiumInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.72f)) {
                            Text("NFC Payment", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(
                                if (selectedCard?.cardType.equals("rupay", true)) {
                                    "RuPay, wallet, and bank-ready mock tap flow"
                                } else {
                                    "Select a RuPay card to unlock NFC payment"
                                },
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Contactless, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                CardDeck(
                    cards = cards,
                    selectedCard = selectedCard,
                    blurAmount = deckBlur,
                    expanded = selectedCardExpanded,
                    displayBalance = animatedBalance,
                    awaitingTap = awaitingTap,
                    onSelectCard = { card ->
                        nfcViewModel.selectCard(card)
                        nfcViewModel.resetReadyState()
                        selectedCardExpanded = true
                        showTapPrompt = true
                        awaitingTap = paymentValue > 0.0
                    },
                    onTapSelected = { card ->
                        if (selectedCard?.cardId == card.cardId && awaitingTap) {
                            nfcViewModel.processNFCPayment(paymentAmount)
                        }
                    }
                )

                selectedCard?.let { card ->
                    PremiumInfoCard {
                        Text("Selected Card", fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                                Text("${card.issuer} • ${card.cardType}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Available balance", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                Text("INR ${"%,.0f".format(animatedBalance)}", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            }
                            Text(
                                if (card.cardType.equals("rupay", true)) "RuPay NFC" else "No NFC",
                                color = if (card.cardType.equals("rupay", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text("Card ****${card.cardNumber.takeLast(4)}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("Expiry ${card.expiry}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }

                PremiumInfoCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = paymentAmount,
                            onValueChange = { nfcViewModel.setPaymentAmount(it.filter { ch -> ch.isDigit() || ch == '.' }) },
                            label = { Text("Enter amount") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("403", "999", "6,345", "19,515").forEach { amount ->
                                PremiumPill(
                                    text = amount,
                                    selected = paymentAmount == amount.replace(",", ""),
                                    modifier = Modifier.fillMaxWidth(0.23f)
                                ) {
                                    nfcViewModel.setPaymentAmount(amount.replace(",", ""))
                                }
                            }
                        }
                        Text(
                            when {
                                nfcStatus == "Scanning" -> "Hold your selected card near the reader"
                                nfcStatus == "Processing" -> "Debiting INR ${paymentAmount.ifBlank { "0" }}"
                                nfcStatus == "Success" -> "Payment completed successfully"
                                awaitingTap -> "Tap the selected card to pay"
                                else -> "Select a card and tap Ready To Tap"
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            UdhaarPayButton(
                                text = "Ready To Tap",
                                onClick = {
                                    awaitingTap = true
                                    nfcViewModel.resetReadyState()
                                },
                                enabled = selectedCard != null && paymentValue > 0.0 && nfcStatus !in listOf("Scanning", "Processing"),
                                modifier = Modifier.fillMaxWidth(0.64f)
                            )
                            UdhaarPayButton(
                                text = "Reset",
                                onClick = {
                                    nfcViewModel.setPaymentAmount("")
                                    nfcViewModel.resetReadyState()
                                    awaitingTap = false
                                },
                                modifier = Modifier.fillMaxWidth(0.34f)
                            )
                        }
                    }
                }

                PremiumInfoCard {
                    Text("How the animation works", fontWeight = FontWeight.SemiBold)
                    Text("The chosen card rises to the front, the others blur behind it, and the balance counts down during the tap.")
                    Text("On success the card shatters into pieces and dust before the stack settles back down.")
                }
            }

            if (nfcStatus in listOf("Scanning", "Processing")) {
                NfcProcessingOverlay(
                    amount = paymentValue,
                    displayBalance = animatedBalance
                )
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
}

@Composable
private fun TopBar(
    title: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Box(modifier = Modifier.padding(10.dp)) {
                Icon(Icons.Default.Contactless, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun CardDeck(
    cards: List<CreditCard>,
    selectedCard: CreditCard?,
    blurAmount: androidx.compose.ui.unit.Dp,
    expanded: Boolean,
    displayBalance: Int,
    awaitingTap: Boolean,
    onSelectCard: (CreditCard) -> Unit,
    onTapSelected: (CreditCard) -> Unit
) {
    if (cards.isEmpty()) {
        PremiumInfoCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Text("No cards available", fontWeight = FontWeight.SemiBold)
                Text("Add a RuPay card from Cards to use NFC.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }
        return
    }

    val selectedId = selectedCard?.cardId ?: cards.first().cardId
    val selectedIndex = cards.indexOfFirst { it.cardId == selectedId }.coerceAtLeast(0)
    val cardCameraDistancePx = with(LocalDensity.current) { 22.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp),
        contentAlignment = Alignment.Center
    ) {
        if (awaitingTap) {
            WifiTapRings()
        }

        cards.forEachIndexed { index, card ->
            val isSelected = card.cardId == selectedId
            val relative = index - selectedIndex
            val targetX = if (expanded) (relative * 58).dp else 0.dp
            val targetY = if (expanded) {
                if (isSelected) (-8).dp else 26.dp
            } else {
                (index * 12).dp
            }
            val targetRotation = if (expanded) {
                (relative * 4.5f).coerceIn(-14f, 14f)
            } else {
                if (index % 2 == 0) -4f else 4f
            }
            val targetScale = if (expanded) {
                if (isSelected) 1.06f else 0.82f
            } else {
                (0.92f + index * 0.02f).coerceAtMost(0.98f)
            }
            val targetAlpha = if (expanded && !isSelected) 0.42f else 1f

            val animX by animateDpAsState(targetValue = targetX, animationSpec = tween(420), label = "deck_x_$index")
            val animY by animateDpAsState(targetValue = targetY, animationSpec = tween(420), label = "deck_y_$index")
            val animScale by animateFloatAsState(targetValue = targetScale, animationSpec = tween(420), label = "deck_scale_$index")
            val animRot by animateFloatAsState(targetValue = targetRotation, animationSpec = tween(420), label = "deck_rot_$index")
            val animAlpha by animateFloatAsState(targetValue = targetAlpha, animationSpec = tween(420), label = "deck_alpha_$index")
            val elevation by animateFloatAsState(
                targetValue = if (isSelected && expanded) 32f else 12f,
                animationSpec = tween(360),
                label = "deck_elevation_$index"
            )
            val cardWidth = if (isSelected && expanded) 245.dp else 230.dp
            val cardHeight = if (isSelected && expanded) 315.dp else 145.dp
            val balanceLabel = if (isSelected && expanded) "Available balance" else "Current balance"

            PremiumCard(
                card = card,
                displayBalance = if (isSelected) displayBalance else card.balance.toInt(),
                balanceLabel = balanceLabel,
                selected = isSelected,
                amountVisible = isSelected && expanded,
                modifier = Modifier
                    .offset(x = animX, y = animY)
                    .zIndex(if (isSelected) 100f else index.toFloat())
                    .graphicsLayer {
                        scaleX = animScale
                        scaleY = animScale
                        rotationZ = if (isSelected && expanded) 0f else animRot
                        rotationX = if (expanded) 8f else 5f
                        alpha = animAlpha
                        shadowElevation = elevation
                        cameraDistance = cardCameraDistancePx
                    }
                    .blur(if (isSelected) 0.dp else blurAmount)
                    .clickable {
                        if (!expanded) {
                            onSelectCard(card)
                        } else if (isSelected) {
                            onTapSelected(card)
                        } else {
                            onSelectCard(card)
                        }
                    },
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                swipeHint = when {
                    isSelected && awaitingTap -> "Tap this card"
                    isSelected -> "Selected"
                    else -> "Tap to focus"
                }
            )
        }

        Text(
            text = when {
                !expanded -> "Tap deck to open cards"
                awaitingTap -> "Tap the selected card to pay"
                else -> "Cards are blurred until selected"
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
        )
    }
}

@Composable
private fun PremiumCard(
    card: CreditCard,
    displayBalance: Int,
    balanceLabel: String,
    selected: Boolean,
    amountVisible: Boolean,
    modifier: Modifier = Modifier,
    cardWidth: androidx.compose.ui.unit.Dp,
    cardHeight: androidx.compose.ui.unit.Dp,
    swipeHint: String
) {
    val accent = if (card.cardType.equals("rupay", true)) {
        Brush.linearGradient(listOf(Color(0xFF2C224B), Color(0xFF5A4C90), Color(0xFFE7C849)))
    } else {
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)))
    }
    Surface(
        modifier = modifier
            .size(cardWidth, cardHeight)
            .clip(RoundedCornerShape(if (selected) 28.dp else 22.dp)),
        color = Color.Transparent,
        shape = RoundedCornerShape(if (selected) 28.dp else 22.dp),
        shadowElevation = 12.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(accent)
                .padding(16.dp)
        ) {
            if (selected && amountVisible) {
                SelectedAmountCard(card = card, displayBalance = displayBalance, balanceLabel = balanceLabel, swipeHint = swipeHint)
            } else {
                LandscapeCard(card = card, displayBalance = displayBalance, balanceLabel = balanceLabel)
            }
        }
    }
}

@Composable
private fun LandscapeCard(
    card: CreditCard,
    displayBalance: Int,
    balanceLabel: String
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(balanceLabel.uppercase(), color = Color.White.copy(alpha = 0.78f), fontSize = 10.sp)
                Text(card.issuer, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
        }
        Column {
            Text(
                "INR ${"%,.2f".format(displayBalance.toDouble())}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
            Text("**** **** **** ${card.cardNumber.takeLast(4)}", color = Color.White.copy(alpha = 0.84f), fontSize = 12.sp)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(card.cardType.uppercase(), color = Color.White.copy(alpha = 0.82f), fontSize = 11.sp)
            Text("EXP ${card.expiry}", color = Color.White.copy(alpha = 0.82f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun SelectedAmountCard(
    card: CreditCard,
    displayBalance: Int,
    balanceLabel: String,
    swipeHint: String
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("HOLD NEAR READER", color = Color.White.copy(alpha = 0.75f), fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.14f)) {
                Text(card.cardType.uppercase(), modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = Color.White, fontSize = 10.sp)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(card.issuer, color = Color.White.copy(alpha = 0.88f), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Text(balanceLabel.uppercase(), color = Color.White.copy(alpha = 0.72f), fontSize = 10.sp)
            Text(
                "INR ${"%,.2f".format(displayBalance.toDouble())}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Spacer(Modifier.height(10.dp))
            Text("Card ****${card.cardNumber.takeLast(4)}", color = Color.White.copy(alpha = 0.84f), fontSize = 12.sp)
        }
        Text(
            swipeHint.uppercase(),
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
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
        drawCircle(color = ringColor, radius = base * r1, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f))
        drawCircle(color = ringColor.copy(alpha = 0.22f), radius = base * r2, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
        drawCircle(color = ringColor.copy(alpha = 0.16f), radius = base * (1.05f + (r2 - 0.68f)), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
    }
}

@Composable
private fun NfcProcessingOverlay(
    amount: Double,
    displayBalance: Int
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val transition = rememberInfiniteTransition(label = "nfc_transition")
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.16f,
        animationSpec = infiniteRepeatable(animation = tween(650), repeatMode = RepeatMode.Reverse),
        label = "pulse"
    )
    val ripple by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1100), repeatMode = RepeatMode.Restart),
        label = "ripple"
    )
    val moneyFlight = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        moneyFlight.animateTo(1f, tween(1250, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.56f))
    ) {
        CardShatterBackdrop(progress = ripple)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(240.dp).blur(3.dp)) {
                val base = size.minDimension / 2f
                drawCircle(color = primaryColor.copy(alpha = 0.22f), radius = base * pulse)
                drawCircle(color = secondaryColor.copy(alpha = 0.22f * (1f - ripple)), radius = base * (0.55f + ripple))
                drawCircle(color = primaryColor.copy(alpha = 0.15f * (1f - ripple)), radius = base * (0.82f + ripple * 0.8f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f))
            }

            Icon(Icons.Default.Contactless, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
            Text(
                "INR ${"%,.2f".format(amount)}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.offset(y = (-98).dp)
            )
            Text(
                "Card balance: INR ${"%,.0f".format(displayBalance.toDouble())}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                modifier = Modifier.offset(y = 90.dp)
            )
            Icon(
                Icons.Default.CurrencyRupee,
                contentDescription = null,
                tint = secondaryColor,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (moneyFlight.value * 130f).roundToInt(),
                            y = (-moneyFlight.value * 80f).roundToInt()
                        )
                    }
                    .size(24.dp)
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
    val checkScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(380),
        label = "check_scale"
    )
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val confettiProgress = remember { Animatable(0f) }
    LaunchedEffect(amount) {
        confettiProgress.snapTo(0f)
        confettiProgress.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.48f))
    ) {
        CardShatterBackdrop(progress = confettiProgress.value)
        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(26) { idx ->
                val x = (size.width / 28f) * (idx + 1)
                val y = (confettiProgress.value * size.height * 0.65f) + Random(idx).nextInt(-40, 60)
                drawCircle(
                    color = if (idx % 2 == 0) secondaryColor else primaryColor,
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
            Text("INR ${"%.2f".format(amount)} Paid Successfully", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text("Card ****$cardLast4 updated.", color = secondaryColor, fontWeight = FontWeight.SemiBold)
            AnimatedVisibility(visible = showRewardFlight) {
                Row(modifier = Modifier.offset(y = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = secondaryColor)
                    Text("Cashback +INR ${"%.2f".format(reward)}", color = secondaryColor, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.size(8.dp))
                    Icon(Icons.Default.Wallet, contentDescription = null, tint = primaryColor)
                }
            }
        }
    }
}

@Composable
private fun CardShatterBackdrop(progress: Float) {
    val accent = MaterialTheme.colorScheme.primary
    val glow = MaterialTheme.colorScheme.secondary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cardWidth = size.width * 0.72f
        val cardHeight = size.height * 0.22f
        val left = center.x - cardWidth / 2f
        val top = center.y - cardHeight / 2f - size.height * 0.08f
        val rows = 4
        val columns = 6
        val pieceWidth = cardWidth / columns
        val pieceHeight = cardHeight / rows
        val scale = 1f + progress * 0.06f

        drawRoundRect(
            color = surfaceVariantColor.copy(alpha = 0.12f * (1f - progress)),
            topLeft = androidx.compose.ui.geometry.Offset(left, top),
            size = androidx.compose.ui.geometry.Size(cardWidth, cardHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
        )

        repeat(rows) { row ->
            repeat(columns) { column ->
                val seed = row * columns + column
                val pieceLeft = left + column * pieceWidth
                val pieceTop = top + row * pieceHeight
                val originX = pieceLeft + pieceWidth / 2f
                val originY = pieceTop + pieceHeight / 2f
                val directionX = (column - (columns - 1) / 2f) * 10f
                val directionY = (row - (rows - 1) / 2f) * 14f - 8f
                val offsetX = directionX * progress * 6f
                val offsetY = directionY * progress * 6f + progress * 28f
                val alpha = (1f - progress * 1.15f).coerceAtLeast(0f)
                val jitter = (seed % 3 - 1) * 3f * progress

                withTransform({
                    translate(offsetX + jitter, offsetY)
                    scale(scale, scale, androidx.compose.ui.geometry.Offset(originX, originY))
                }) {
                    val pieceColor = when (seed % 3) {
                        0 -> accent.copy(alpha = alpha * 0.72f)
                        1 -> glow.copy(alpha = alpha * 0.62f)
                        else -> Color.White.copy(alpha = alpha * 0.68f)
                    }
                    drawRoundRect(
                        color = pieceColor,
                        topLeft = androidx.compose.ui.geometry.Offset(pieceLeft, pieceTop),
                        size = androidx.compose.ui.geometry.Size(pieceWidth - 1f, pieceHeight - 1f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )
                }
            }
        }
    }
}
