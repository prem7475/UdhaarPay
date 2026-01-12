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
import com.udhaarpay.app.ui.viewmodel.BankAccountViewModel

@Composable
fun BankAccountScreen(viewModel: BankAccountViewModel = androidx.hilt.navigation.compose.hiltViewModel()) {
    val accounts by viewModel.accounts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Bank Accounts", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(12.dp))
            Button(onClick = { showAddDialog = true }, shape = RoundedCornerShape(12.dp)) {
                Text("Add Bank Account")
            }
            Spacer(Modifier.height(12.dp))
            if (accounts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No bank accounts found", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(accounts) { account ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF232946))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(account.bankName, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(account.accountNumber, color = Color.LightGray, fontSize = 13.sp)
                                    Text(account.ifscCode, color = Color.LightGray, fontSize = 13.sp)
                                }
                                IconButton(onClick = { viewModel.deleteBankAccount(account) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showAddDialog) {
            AddBankAccountDialog(
                onAdd = { bankName, accountNumber, ifsc, holderName ->
                    val newAccount = com.udhaarpay.app.data.local.entities.BankAccount(
                        bankName = bankName,
                        accountNumber = accountNumber,
                        ifscCode = ifsc,
                        accountType = "Savings",
                        balance = 0.0,
                        nickname = null,
                        addedDate = System.currentTimeMillis()
                    )
                    viewModel.addBankAccount(newAccount)
                    showAddDialog = false
                },
                onDismiss = { showAddDialog = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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