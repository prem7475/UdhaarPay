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

import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.ui.viewmodel.InsuranceViewModel

@Composable
fun InsuranceScreen(viewModel: InsuranceViewModel = hiltViewModel()) {
    val policies by viewModel.insurances.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(20.dp)
    ) {
        Text("Your Policies", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
        Spacer(Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(policies) { policy ->
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
                            Text(policy.policyType, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                            Text("Coverage: ${policy.coverage}", fontSize = 14.sp, color = Color(0xFF22C55E))
                            Text("Expiry: ${policy.expiryDate}", fontSize = 13.sp, color = Color(0xFFCBD5E1))
                        }
                        Text(policy.status, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (policy.status == "Active") Color(0xFF22C55E) else Color(0xFFDC2626))
                    }
                }
            }
        }
    }
}