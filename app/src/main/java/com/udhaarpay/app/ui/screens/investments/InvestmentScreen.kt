package com.udhaarpay.app.ui.screens.investments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.udhaarpay.app.ui.viewmodel.InvestmentViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InvestmentScreen(viewModel: InvestmentViewModel = hiltViewModel()) {
    val investments by viewModel.investments.collectAsState()
    val summary by viewModel.summary.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Investments",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "Grow your wealth with curated investment options.",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 18.dp)
        )
        Surface(
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 2.dp,
            shadowElevation = 6.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Your Portfolio", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text("Total Value: ₹${summary?.toInt() ?: 0}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Text("Investments: ${investments.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(22.dp))
        Text("Your Investments", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            items(investments) { inv ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    shadowElevation = 4.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(inv.type, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        Text("Broker: ${inv.brokerName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Current Value: ₹${inv.currentValue ?: 0.0}", fontSize = 13.sp, color = Color(0xFF22C55E))
                        Text("Invested: ₹${inv.amount}", fontSize = 13.sp, color = Color(0xFFCBD5E1))
                    }
                }
            }
        }
    }
}


@Composable
fun InvestmentOptionCard(title: String, desc: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .height(90.dp)
            .clickable { onClick() }
    ) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            Text(desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
