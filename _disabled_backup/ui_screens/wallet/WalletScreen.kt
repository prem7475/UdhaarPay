package com.example.udhaarpay.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.ExpenseViewModel

@Composable
fun WalletScreen(
    onBack: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var transactionType by remember { mutableStateOf<TransactionType?>(null) } // Add or Deduct

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            SmallTopAppBar(
                title = { Text("My Wallet", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = PureBlack)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cash Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkZinc),
                border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text("Cash in Hand", color = Zinc400, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "₹${state.walletBalance}",
                            color = White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(Zinc800, RoundedCornerShape(12.dp))
                            .clickable { showEditDialog = true }
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Add/Deduct Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    text = "Add Cash",
                    icon = Icons.Default.Add,
                    color = Color.Green,
                    onClick = { transactionType = TransactionType.Add }
                )
                
                ActionButton(
                    text = "Deduct Cash",
                    icon = Icons.Default.Remove,
                    color = Color.Red,
                    onClick = { transactionType = TransactionType.Deduct }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Updating your cash balance helps in tracking manual expenses better.",
                color = Zinc400,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        if (showEditDialog) {
            EditBalanceDialog(
                currentBalance = state.walletBalance,
                onDismiss = { showEditDialog = false },
                onSave = { newBalance ->
                    viewModel.updateWalletBalance(newBalance)
                    showEditDialog = false
                }
            )
        }
        
        transactionType?.let { type ->
            TransactionDialog(
                type = type,
                onDismiss = { transactionType = null },
                onConfirm = { amount ->
                    val current = state.walletBalance
                    val newBalance = if (type == TransactionType.Add) current + amount else current - amount
                    viewModel.updateWalletBalance(newBalance)
                    transactionType = null
                }
            )
        }
    }
}

enum class TransactionType { Add, Deduct }

@Composable
fun ActionButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text, color = White, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TransactionDialog(
    type: TransactionType,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkZinc),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (type == TransactionType.Add) "Add Cash" else "Deduct Cash",
                    color = White,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = if (type == TransactionType.Add) Color.Green else Color.Red,
                        unfocusedBorderColor = Zinc800
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Zinc400)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            amount.toDoubleOrNull()?.let { onConfirm(it) }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == TransactionType.Add) Color.Green else Color.Red
                        )
                    ) {
                        Text(if (type == TransactionType.Add) "Add" else "Deduct", color = PureBlack)
                    }
                }
            }
        }
    }
}

@Composable
fun EditBalanceDialog(
    currentBalance: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var balance by remember { mutableStateOf(currentBalance.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkZinc),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Update Cash Balance", color = White, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = balance,
                    onValueChange = { balance = it },
                    label = { Text("Total Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Zinc800
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Zinc400)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            balance.toDoubleOrNull()?.let { onSave(it) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
}
