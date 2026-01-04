package com.udhaarpay.app.ui.screens.insurance

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

val insuranceTypes = listOf("Health Insurance", "Life Insurance", "Car Insurance", "Travel Insurance", "Home Insurance")
val mockPolicies = listOf(
    Policy("Health Insurance", "Active", "₹5,00,000", "2026-01-01"),
    Policy("Car Insurance", "Expired", "₹2,00,000", "2024-12-31")
)
data class Policy(val type: String, val status: String, val coverage: String, val expiry: String)

@Composable
fun InsuranceScreen() {
    var selectedType by remember { mutableStateOf<String?>(null) }
    var showBuy by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(20.dp)
    ) {
        Text("Your Policies", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
        Spacer(Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(mockPolicies) { policy ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(policy.type, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                            Text("Coverage: ${policy.coverage}", fontSize = 14.sp, color = Color(0xFF22C55E))
                            Text("Expiry: ${policy.expiry}", fontSize = 13.sp, color = Color(0xFFCBD5E1))
                        }
                        Text(policy.status, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (policy.status == "Active") Color(0xFF22C55E) else Color(0xFFDC2626))
                    }
                }
            }
        }
    }
}