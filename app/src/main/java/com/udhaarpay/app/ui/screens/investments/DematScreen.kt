package com.udhaarpay.app.ui.screens.investments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.local.entities.Investment
import com.udhaarpay.app.ui.viewmodel.InvestmentViewModel

private data class Holding(val symbol: String, val qty: Int, val price: Double)

@Composable
fun DematScreen(viewModel: InvestmentViewModel = hiltViewModel()) {
    var broker by remember { mutableStateOf("Zerodha") }
    var accountType by remember { mutableStateOf("Investment Account") }
    var accountOpened by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }

    val holdings = listOf(
        Holding("Reliance", 10, 2500.0),
        Holding("TCS", 5, 3500.0),
        Holding("HDFC Bank", 8, 1500.0),
        Holding("Infosys", 3, 2000.0)
    )
    val portfolioValue = holdings.sumOf { it.qty * it.price }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Demat Account", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = broker,
            onValueChange = { broker = it },
            label = { Text("Broker") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = accountType,
            onValueChange = { accountType = it },
            label = { Text("Account Type (Trading/Investment/Demo)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                accountOpened = true
                viewModel.insert(
                    Investment(
                        brokerName = broker,
                        fundName = "Demat Portfolio",
                        type = "demat",
                        amount = portfolioValue,
                        frequency = null,
                        date = System.currentTimeMillis(),
                        currentValue = portfolioValue,
                        returns = 0.0
                    )
                )
                status = "Demat account opened (DP${System.currentTimeMillis().toString().takeLast(5)})"
            }) {
                Text("Open Demat")
            }
            Button(onClick = { status = "Mock KYC submitted for $broker" }) {
                Text("Submit KYC")
            }
        }

        if (!status.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(status ?: "", color = MaterialTheme.colorScheme.primary)
        }

        if (accountOpened) {
            Spacer(Modifier.height(14.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text("Account: DP12345", fontWeight = FontWeight.SemiBold)
                    Text("Status: Active")
                    Text("Portfolio: INR ${"%.2f".format(portfolioValue)}")
                }
            }
            Spacer(Modifier.height(10.dp))
            Text("Holdings", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(holdings) { holding ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(holding.symbol, fontWeight = FontWeight.SemiBold)
                                Text("${holding.qty} shares")
                            }
                            Text("INR ${holding.qty * holding.price}")
                        }
                    }
                }
            }
        }
    }
}
