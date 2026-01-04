package com.example.udhaarpay.ui.screens.billpayments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MobileRechargeScreen() {
    var phoneNumber by remember { mutableStateOf("") }
    var selectedOperator by remember { mutableStateOf<String?>(null) }
    var selectedPlan by remember { mutableStateOf<String?>(null) }
    var showConfirmation by remember { mutableStateOf(false) }
    val operators = listOf("Jio", "Airtel", "Vi", "BSNL")
    val plans = listOf(
        "₹199 - 28 days - 1.5GB/day",
        "₹399 - 56 days - 1.5GB/day",
        "₹599 - 84 days - 2GB/day",
        "₹719 - 84 days - 2GB/day + OTT"
    )
    val offers = listOf("10% Cashback on HDFC Cards", "5% off on UPI payments")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mobile Recharge", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        if (phoneNumber.length >= 10) {
            selectedOperator = operators[(phoneNumber.last().digitToIntOrNull() ?: 0) % operators.size]
            Text("Operator: ${selectedOperator}", color = Color(0xFF2563EB), fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(16.dp))
        Text("Select Plan", fontWeight = FontWeight.Bold)
        LazyColumn(modifier = Modifier.height(180.dp)) {
            items(plans) { plan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedPlan = plan },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPlan == plan) Color(0xFFDCFCE7) else Color.White
                    )
                ) {
                    Box(Modifier.padding(12.dp)) { Text(plan) }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Special Offers", fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            offers.forEach { offer ->
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))) {
                    Box(Modifier.padding(8.dp)) { Text(offer, fontSize = 12.sp) }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        if (selectedPlan != null && phoneNumber.length >= 10) {
            Button(
                onClick = { showConfirmation = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Recharge Now")
            }
        }
        if (showConfirmation) {
            AlertDialog(
                onDismissRequest = { showConfirmation = false },
                title = { Text("Recharge Successful") },
                text = { Text("${selectedPlan} for $phoneNumber completed!") },
                confirmButton = {
                    TextButton(onClick = { showConfirmation = false }) { Text("OK") }
                }
            )
        }
    }
}
