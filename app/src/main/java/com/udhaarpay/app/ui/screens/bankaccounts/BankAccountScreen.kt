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
// All usages of BankAccount are commented out for compatibility
import java.util.UUID

@Composable
fun BankAccountScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Bank account feature temporarily unavailable", color = Color.White)
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