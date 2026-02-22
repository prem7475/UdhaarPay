package com.udhaarpay.app.ui.screens.investments

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.local.entities.Investment
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.components.UdhaarPayTextButton
import com.udhaarpay.app.ui.screens.common.InAppBrowserDialog
import com.udhaarpay.app.ui.theme.LossRed
import com.udhaarpay.app.ui.theme.ProfitGreen
import com.udhaarpay.app.ui.viewmodel.InvestmentViewModel
import java.util.Locale

private data class Broker(val name: String, val url: String)
private data class MarketOption(val title: String, val subtitle: String, val url: String)

@Composable
fun InvestmentScreen(
    onOpenDemat: () -> Unit = {},
    onOpenBonds: () -> Unit = {},
    onOpenNews: () -> Unit = {},
    onOpenPaperTrading: () -> Unit = {},
    viewModel: InvestmentViewModel = hiltViewModel()
) {
    val investments by viewModel.investments.collectAsState()
    val totalInvested by viewModel.totalInvested.collectAsState()
    val totalCurrent by viewModel.totalCurrent.collectAsState()
    val profitLoss by viewModel.profitLoss.collectAsState()
    val status by viewModel.statusMessage.collectAsState()

    val brokers = listOf(
        Broker("Zerodha", "https://zerodha.com/"),
        Broker("Groww", "https://groww.in/"),
        Broker("Upstox", "https://upstox.com/"),
        Broker("Angel One", "https://www.angelone.in/"),
        Broker("5Paisa", "https://www.5paisa.com/"),
        Broker("ICICI Direct", "https://www.icicidirect.com/"),
        Broker("Paytm Money", "https://www.paytmmoney.com/"),
        Broker("Kuvera", "https://kuvera.in/"),
        Broker("ET Money", "https://www.etmoney.com/"),
        Broker("Smallcase", "https://www.smallcase.com/")
    )

    val marketOptions = listOf(
        MarketOption("NSE Equity", "Live listed equities", "https://www.nseindia.com/market-data/live-equity-market"),
        MarketOption("NSE Futures", "Futures market", "https://www.nseindia.com/market-data/equity-derivatives-watch"),
        MarketOption("NSE Options Chain", "Calls and puts", "https://www.nseindia.com/option-chain"),
        MarketOption("BSE Market", "Stocks and indices", "https://www.bseindia.com/"),
        MarketOption("ETF Market", "Exchange traded funds", "https://www.nseindia.com/market-data/exchange-traded-funds-etf"),
        MarketOption("IPO Center", "Current IPO subscriptions", "https://www.nseindia.com/market-data/all-upcoming-issues-ipo"),
        MarketOption("SGB / G-Sec", "Government securities", "https://www.rbi.org.in/Scripts/BS_PressReleaseDisplay.aspx?prid="),
        MarketOption("SEBI Investor", "Investor education and alerts", "https://investor.sebi.gov.in/")
    )

    var selectedBroker by remember { mutableStateOf(brokers.first()) }
    var selectedBrowserTitle by remember { mutableStateOf<String?>(null) }
    var selectedBrowserUrl by remember { mutableStateOf<String?>(null) }
    var fundName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("SIP") }
    var frequency by remember { mutableStateOf("Monthly") }
    var error by remember { mutableStateOf<String?>(null) }

    val pnlProgress by animateFloatAsState(
        targetValue = if (totalInvested == 0.0) 0f else (totalCurrent / totalInvested).toFloat().coerceAtLeast(0f),
        label = "pnl_progress"
    )

    LazyColumn(
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Investment Desk", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Professional market access + local portfolio tracking")
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Text("Portfolio Summary", fontWeight = FontWeight.SemiBold)
                    Text("Total Invested: INR ${"%.2f".format(totalInvested)}")
                    Text("Current Value: INR ${"%.2f".format(totalCurrent)}")
                    Text(
                        "Profit/Loss: INR ${"%.2f".format(profitLoss)}",
                        color = if (profitLoss >= 0) ProfitGreen else LossRed
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height((82 * pnlProgress.coerceAtMost(1.6f)).dp)
                                .align(Alignment.Bottom)
                        ) {
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {}
                        }
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height((82 * (1f - (profitLoss / (totalInvested + 1)).toFloat().coerceIn(-0.8f, 0.8f))).dp)
                                .align(Alignment.Bottom)
                        ) {
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {}
                        }
                        Text("Live P&L snapshot", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                UdhaarPayButton(text = "Open Demat", onClick = onOpenDemat, modifier = Modifier.weight(1f))
                UdhaarPayButton(text = "Bonds", onClick = onOpenBonds, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                UdhaarPayButton(text = "News", onClick = onOpenNews, modifier = Modifier.weight(1f))
                UdhaarPayButton(text = "Paper Trading", onClick = onOpenPaperTrading, modifier = Modifier.weight(1f))
            }
        }

        item {
            Text("Market Options", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        items(marketOptions) { option ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(option.title, fontWeight = FontWeight.SemiBold)
                        Text(option.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                    UdhaarPayTextButton(
                        text = "Open",
                        onClick = {
                            selectedBrowserTitle = option.title
                            selectedBrowserUrl = option.url
                        }
                    )
                }
            }
        }

        item {
            Text("Brokers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        items(brokers) { broker ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(broker.name)
                    Row {
                        UdhaarPayTextButton(text = "Select", onClick = { selectedBroker = broker })
                        UdhaarPayTextButton(
                            text = "Open",
                            onClick = {
                                selectedBrowserTitle = broker.name
                                selectedBrowserUrl = broker.url
                            }
                        )
                    }
                }
            }
        }

        item {
            Text("Add Investment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = fundName,
                onValueChange = { fundName = it },
                label = { Text("Fund / Stock / Instrument Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Type (SIP / Mutual / Bonds / Equity / Futures / Options)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = frequency,
                onValueChange = { frequency = it },
                label = { Text("Frequency") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            UdhaarPayButton(
                text = "Save Investment",
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    when {
                        fundName.isBlank() -> error = "Instrument name is required"
                        amountValue == null || amountValue <= 0 -> error = "Enter valid amount"
                        else -> {
                            error = null
                            viewModel.insert(
                                Investment(
                                    brokerName = selectedBroker.name,
                                    fundName = fundName,
                                    type = type.lowercase(Locale.getDefault()),
                                    amount = amountValue,
                                    frequency = frequency,
                                    date = System.currentTimeMillis(),
                                    currentValue = amountValue,
                                    returns = 0.0
                                )
                            )
                            fundName = ""
                            amount = ""
                            type = "SIP"
                            frequency = "Monthly"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (!error.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(error ?: "", color = MaterialTheme.colorScheme.error)
            }
            if (!status.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(status ?: "", color = MaterialTheme.colorScheme.primary)
                UdhaarPayTextButton(text = "Dismiss", onClick = { viewModel.clearStatus() })
            }
        }

        item {
            Text("My Investments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        items(investments.sortedByDescending { it.date }) { inv ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(inv.fundName ?: "-", fontWeight = FontWeight.SemiBold)
                    Text("${inv.brokerName} | ${inv.type.uppercase(Locale.getDefault())}")
                    Text("Invested: INR ${"%.2f".format(inv.amount)}")
                    Text("Current: INR ${"%.2f".format(inv.currentValue ?: inv.amount)}")
                    Text(
                        "Returns: INR ${"%.2f".format(inv.returns ?: 0.0)}",
                        color = if ((inv.returns ?: 0.0) >= 0) ProfitGreen else LossRed
                    )
                }
            }
        }
    }

    if (selectedBrowserTitle != null && selectedBrowserUrl != null) {
        InAppBrowserDialog(
            title = selectedBrowserTitle.orEmpty(),
            url = selectedBrowserUrl.orEmpty(),
            onDismiss = {
                selectedBrowserTitle = null
                selectedBrowserUrl = null
            }
        )
    }
}

