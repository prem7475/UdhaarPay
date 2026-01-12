package com.udhaarpay.app.ui.screens.debt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.ui.viewmodel.DebtViewModel

@Composable
fun DebtScreen(viewModel: DebtViewModel = hiltViewModel()) {
    var showPay by remember { mutableStateOf(false) }
    val debts by viewModel.debts.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(20.dp)
    ) {
        Text(
            "Your Debts",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color(0xFF2563EB),
            modifier = Modifier.padding(bottom = 10.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(debts) { debt ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 6.dp,
                    color = Color(0xFF1E293B)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(debt.type, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                            Spacer(Modifier.weight(1f))
                            Text(
                                debt.status,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = when (debt.status) {
                                    "Active" -> Color(0xFF22C55E)
                                    "Overdue" -> Color(0xFFDC2626)
                                    else -> Color(0xFFCBD5E1)
                                },
                                modifier = Modifier.background(
                                    color = when (debt.status) {
                                        "Active" -> Color(0xFF16213E)
                                        "Overdue" -> Color(0xFF3B1F1F)
                                        else -> Color(0xFF1E293B)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ).padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Amount: ₹${debt.amount}", fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color(0xFF22C55E))
                        Text("Date: ${java.text.SimpleDateFormat("dd MMM yyyy").format(java.util.Date(debt.date))}", fontSize = 13.sp, color = Color(0xFFCBD5E1))
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = { showPay = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                        ) {
                            Text("Pay Now", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}