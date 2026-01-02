package com.example.udhaarpay.ui.screens.nfc

import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.data.local.entity.CreditCardEntity
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.NFCViewModel
import kotlinx.coroutines.delay

@Composable
fun NFCPaymentScreen(
    onBack: () -> Unit,
    viewModel: NFCViewModel = hiltViewModel()
) {
    val cards by viewModel.cards.collectAsState()
    val selectedCard by viewModel.selectedCard.collectAsState()
    
    // UI State for animations
    var isExpanded by remember { mutableStateOf(false) } // False = Stack, True = Fan Out
    var isReadyToTap by remember { mutableStateOf(false) } // True when a specific card is selected for payment
    
    val context = LocalContext.current
    
    // NFC Check
    LaunchedEffect(Unit) {
        val adapter = NfcAdapter.getDefaultAdapter(context)
        if (adapter == null) {
            Toast.makeText(context, "NFC is not available on this device", Toast.LENGTH_LONG).show()
        } else if (!adapter.isEnabled) {
            Toast.makeText(context, "Please enable NFC in settings", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            SmallTopAppBar(
                title = { Text("NFC Payment", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = PureBlack)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (cards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No cards available", color = Zinc400)
                }
            } else {
                // Determine which view to show
                if (isReadyToTap && selectedCard != null) {
                    ActiveCardView(
                        card = selectedCard!!,
                        onCancel = { isReadyToTap = false }
                    )
                } else {
                    CardStackView(
                        cards = cards,
                        isExpanded = isExpanded,
                        onToggleExpand = { isExpanded = !isExpanded },
                        onSelectCard = { card ->
                            viewModel.selectCard(card)
                            isReadyToTap = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CardStackView(
    cards: List<CreditCardEntity>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelectCard: (CreditCardEntity) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = { if (!isExpanded) onToggleExpand() }),
        contentAlignment = Alignment.Center
    ) {
        cards.forEachIndexed { index, card ->
            // Animation values
            val targetOffset = if (isExpanded) {
                (index - (cards.size - 1) / 2.0f) * 160.0f // Fan out vertically
            } else {
                index * 10.0f // Stacked with slight offset
            }
            
            val animatedOffset by animateFloatAsState(
                targetValue = targetOffset,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            
            val targetScale = if (isExpanded) 1f else 1f - (cards.size - 1 - index) * 0.05f
            val animatedScale by animateFloatAsState(targetScale)
            
            val targetRotation = if (isExpanded) 0f else (index % 2 * 2f - 1f) * 2f // Slight random rotation in stack
            
            NFCCardItem(
                card = card,
                modifier = Modifier
                    .offset(y = animatedOffset.dp)
                    .scale(animatedScale)
                    .graphicsLayer { rotationZ = targetRotation }
                    .zIndex(index.toFloat()) // Ensure correct drawing order
                    .clickable { 
                        if (isExpanded) onSelectCard(card) else onToggleExpand()
                    }
            )
        }
        
        if (!isExpanded) {
            Text(
                "Tap stack to view cards",
                color = Zinc400,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
fun ActiveCardView(
    card: CreditCardEntity,
    onCancel: () -> Unit
) {
    // Pulse Animation for "Ready to Tap"
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Hold near reader to pay",
            color = White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(40.dp))
        
        Box(contentAlignment = Alignment.Center) {
            // Ripple Effect
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .scale(pulseScale)
                    .alpha(pulseAlpha)
                    .background(PrimaryBlue, CircleShape)
            )
            
            // The Card
            NFCCardItem(
                card = card,
                modifier = Modifier.scale(1.1f),
                showDetails = true
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Icon(
            imageVector = Icons.Default.Contactless,
            contentDescription = "NFC",
            tint = PrimaryBlue,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        TextButton(onClick = onCancel) {
            Text("Cancel Payment", color = Zinc400)
        }
    }
}

@Composable
fun NFCCardItem(
    card: CreditCardEntity,
    modifier: Modifier = Modifier,
    showDetails: Boolean = false
) {
    // Card Background Gradient
    val gradient = Brush.linearGradient(
        colors = if (card.isRupay) 
            listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6)) // Blue for Rupay
        else 
            listOf(Color(0xFF581C87), Color(0xFFA855F7)) // Purple for others
    )

    Card(
        modifier = modifier
            .width(300.dp)
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Bank Name
                Text(
                    text = card.name,
                    color = White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Chip Icon
                Box(
                    modifier = Modifier
                        .size(40.dp, 30.dp)
                        .background(Color(0xFFFFD700), RoundedCornerShape(4.dp))
                        .alpha(0.8f)
                )
                
                // Details (Only visible when active/top)
                if (showDetails) {
                    Column {
                        Text(
                            text = "Available Balance",
                            color = White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "₹${card.limit - card.balanceUsed}", // Calculating available limit
                            color = White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Number and Expiry
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "**** **** **** ${card.number.takeLast(4)}",
                        color = White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    if (showDetails) {
                        Text(
                            text = card.expiry,
                            color = White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // Network Logo (Placeholder)
            Text(
                text = if (card.isRupay) "RuPay" else "VISA",
                color = White.copy(alpha = 0.6f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

// Helper for animations
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.zIndex
