package com.udhaarpay.app.ui.screens.bankaccounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete


import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.data.model.BankAccount
import java.util.UUID

@Composable
fun BankAccountScreen(viewModel: BankAccountViewModel = hiltViewModel()) {
    var showAdd by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    val accounts by viewModel.accounts.collectAsState()
    val totalBalance = accounts.sumOf { it.balance }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(20.dp)
    ) {
        Text("Linked Bank Accounts", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
        Spacer(Modifier.height(10.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            color = Color(0xFF2563EB),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.padding(vertical = 18.dp, horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Balance", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Spacer(Modifier.weight(1f))
                Text("₹" + "%,.0f".format(totalBalance), fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
            }
        }
        Spacer(Modifier.height(18.dp))
        Button(onClick = { showAdd = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Add Bank Account")
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(accounts) { acc ->
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
                            Text(acc.bankName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                            Text(acc.accountType + " • " + acc.getMaskedNumber(), fontSize = 15.sp, color = Color(0xFFCBD5E1))
                        }
                        Text("₹" + "%,.0f".format(acc.balance), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF22C55E))
                        Spacer(Modifier.width(12.dp))
                        IconButton(onClick = { viewModel.deleteBankAccount(acc) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
        if (showAdd) {
            AddBankAccountDialog(
                onAdd = { bankName, accountNumber, ifsc, holderName ->
                    val account = BankAccount(
                        bankName = bankName,
                        accountNumber = accountNumber,
                        ifscCode = ifsc,
                        accountHolderName = holderName,
                        userId = UUID.randomUUID().toString(),
                        balance = (10000..100000).random().toDouble()
                    )
                    viewModel.addBankAccount(account)
                    showAdd = false
                    showConfirm = true
                },
                onDismiss = { showAdd = false }
            )
        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Account Added") },
                text = { Text("Your bank account has been added.") },
                confirmButton = { TextButton(onClick = { showConfirm = false }) { Text("OK") } }
            )
        }
    }
}

@Composable
fun AddBankAccountDialog(onAdd: (String, String, String, String) -> Unit, onDismiss: () -> Unit) {
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var ifsc by remember { mutableStateOf("") }
    var holderName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bank Account") },
        text = {
            Column {
                OutlinedTextField(value = bankName, onValueChange = { bankName = it }, label = { Text("Bank Name") })
                OutlinedTextField(value = accountNumber, onValueChange = { accountNumber = it }, label = { Text("Account Number") })
                OutlinedTextField(value = ifsc, onValueChange = { ifsc = it }, label = { Text("IFSC Code") })
                OutlinedTextField(value = holderName, onValueChange = { holderName = it }, label = { Text("Account Holder Name") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (bankName.isNotBlank() && accountNumber.isNotBlank() && ifsc.isNotBlank() && holderName.isNotBlank()) {
                    onAdd(bankName, accountNumber, ifsc, holderName)
                }
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}