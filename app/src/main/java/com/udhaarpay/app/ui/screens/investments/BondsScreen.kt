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

private data class BondOption(
    val name: String,
    val interestRate: Double,
    val duration: String,
    val type: String
)

@Composable
fun BondsScreen(viewModel: InvestmentViewModel = hiltViewModel()) {
    val options = listOf(
        BondOption("India Govt Bond 10Y", 6.2, "10 Years", "government"),
        BondOption("India Govt Bond 5Y", 5.9, "5 Years", "government"),
        BondOption("TCS Corporate Bond", 7.1, "5 Years", "corporate"),
        BondOption("L&T Corporate Bond", 7.4, "7 Years", "corporate")
    )

    var amount by remember { mutableStateOf("1000") }
    var selectedBond by remember { mutableStateOf(options.first()) }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Bonds", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(Modifier.height(10.dp))
        Text("Government & Corporate Bonds", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options) { bond ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(bond.name, fontWeight = FontWeight.SemiBold)
                            Text("${bond.interestRate}% | ${bond.duration}")
                        }
                        Button(onClick = { selectedBond = bond }) {
                            Text(if (selectedBond == bond) "Selected" else "Select")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Investment Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull() ?: 0.0
                if (amountValue < 1000.0) {
                    message = "Minimum investment is INR 1000"
                } else {
                    viewModel.insert(
                        Investment(
                            brokerName = selectedBond.name,
                            fundName = "${selectedBond.type} bond",
                            type = "bond",
                            amount = amountValue,
                            frequency = null,
                            date = System.currentTimeMillis(),
                            currentValue = amountValue,
                            returns = 0.0
                        )
                    )
                    message = "Bond purchase recorded: ${selectedBond.name}"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buy Bond (Mock)")
        }
        if (!message.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(message ?: "", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
        }
    }
}
