package com.example.udhaarpay.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.data.model.BankAccount
import com.example.udhaarpay.data.model.BankCard
import com.example.udhaarpay.ui.components.PremiumButton
import com.example.udhaarpay.ui.components.PremiumTopAppBar
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.WalletViewModel

@Composable
fun WalletScreen(
    onBack: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val cards by viewModel.cards.collectAsState(initial = emptyList())
    val bankAccounts by viewModel.bankAccounts.collectAsState(initial = emptyList())
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground)
    ) {
        PremiumTopAppBar(
            title = "My Wallet",
            onBackClick = onBack
        )

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            containerColor = DarkBackground,
            contentColor = NeonOrange
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "Cards",
                        color = if (selectedTab == 0) NeonOrange else TextTertiary
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "Bank Accounts",
                        color = if (selectedTab == 1) NeonOrange else TextTertiary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> CardsTab(cards = cards)
            1 -> BankAccountsTab(accounts = bankAccounts)
        }
    }
}

@Composable
fun CardsTab(cards: List<BankCard>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (cards.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        CardGradient1Start,
                                        CardGradient1End
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Add Card",
                                tint = TextPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No cards added",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        } else {
            items(cards) { card ->
                WalletCardItem(card = card)
            }
        }
    }
}

@Composable
fun BankAccountsTab(accounts: List<BankAccount>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (accounts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(color = DarkCard),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Add Bank",
                            tint = TextPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No bank accounts added",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(accounts) { account ->
                BankAccountItem(account = account)
            }
        }
    }
}

@Composable
fun WalletCardItem(card: BankCard) {
    var showBalance by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(CardGradient1Start, CardGradient1End)
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = card.bankName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = card.cardType,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Contactless,
                        contentDescription = "NFC",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showBalance = !showBalance },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (showBalance) "₹${card.balance}" else "₹ ••••",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "•••• ${card.lastFourDigits}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    PremiumButton(
                        text = "Check Balance",
                        onClick = { },
                        modifier = Modifier
                            .width(120.dp)
                            .height(36.dp),
                        backgroundColor = NeonOrange.copy(alpha = 0.2f),
                        textColor = NeonOrange
                    )
                }
            }
        }
    }
}

@Composable
fun BankAccountItem(account: BankAccount) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color = NeonOrange.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = "Bank",
                        tint = NeonOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(
                        text = account.bankName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = account.accountNumber.takeLast(4),
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                }

                if (account.isVerified) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = account.accountHolderName,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            PremiumButton(
                text = "Check Balance",
                onClick = { },
                backgroundColor = DarkSurface,
                textColor = NeonOrange
            )
        }
    }
}
