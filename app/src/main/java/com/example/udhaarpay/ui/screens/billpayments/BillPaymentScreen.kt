package com.example.udhaarpay.ui.screens.billpayments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
fun BillPaymentScreen() {
    val billTypes = listOf("Electricity", "Water", "Gas", "Internet", "Mobile Postpaid")
    var selectedBillType by remember { mutableStateOf(billTypes[0]) }
    var accountNumber by remember { mutableStateOf("") }
    var billDetails by remember { mutableStateOf<String?>(null) }
    var paymentSuccess by remember { mutableStateOf(false) }
    var paymentHistory by remember { mutableStateOf(listOf<String>()) }
    var amountToPay by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Bill Payments", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            billTypes.forEach { type ->
                Card(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { selectedBillType = type },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedBillType == type) Color(0xFFDBEAFE) else Color.White
                    )
                ) {
                    Box(Modifier.padding(12.dp)) { Text(type) }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = accountNumber,
            onValueChange = { accountNumber = it },
            label = { Text("Enter ${selectedBillType} Account/Consumer Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                billDetails = "Biller: ${selectedBillType} Board\nAmount: ₹599\nDue: 28th Jan"
                amountToPay = "599"
            },
            enabled = accountNumber.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Fetch Bill") }
        Spacer(Modifier.height(16.dp))
        billDetails?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(it)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            paymentSuccess = true
                            paymentHistory = listOf("${selectedBillType} - ₹$amountToPay - Paid on 3 Jan 2026") + paymentHistory
                            billDetails = null
                            accountNumber = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Pay Now") }
                }
            }
        }
        if (paymentSuccess) {
            AlertDialog(
                onDismissRequest = { paymentSuccess = false },
                title = { Text("Payment Successful") },
                text = { Text("Your ${selectedBillType} bill of ₹$amountToPay has been paid!") },
                confirmButton = {
                    TextButton(onClick = { paymentSuccess = false }) { Text("OK") }
                }
            )
        }
        Spacer(Modifier.height(24.dp))
        Text("Recent Bill Payments", fontWeight = FontWeight.Bold)
        if (paymentHistory.isEmpty()) {
            Text("No bill payments yet.", color = Color.Gray)
        } else {
            LazyColumn(modifier = Modifier.height(120.dp)) {
                items(paymentHistory) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Box(Modifier.padding(12.dp)) { Text(item) }
                    }
                }
            }
        }
    }
}
