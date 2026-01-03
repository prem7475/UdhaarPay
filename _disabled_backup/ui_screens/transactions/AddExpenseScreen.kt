package com.example.udhaarpay.ui.screens.transactions

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.ExpenseViewModel

@Composable
fun AddExpenseScreen(
    onBack: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    // Category Dropdown
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Food") }
    val categories = listOf("Food", "Travel", "Transport", "Salary", "Bills", "Shopping", "Entertainment")

    // Source Dropdown
    var sourceExpanded by remember { mutableStateOf(false) }
    var selectedSource by remember { mutableStateOf("Cash/Wallet") }
    val bankSources = state.bankAccounts.map { it.bankName }
    val allSources = listOf("Cash/Wallet") + bankSources

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            SmallTopAppBar(
                title = { Text("Add Expense", color = White) },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Zinc800
                )
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Zinc800
                    ),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(DarkZinc)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category, color = White) },
                            onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Source Dropdown
            ExposedDropdownMenuBox(
                expanded = sourceExpanded,
                onExpandedChange = { sourceExpanded = !sourceExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedSource,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payment Source") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Zinc800
                    ),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = sourceExpanded,
                    onDismissRequest = { sourceExpanded = false },
                    modifier = Modifier.background(DarkZinc)
                ) {
                    allSources.forEach { source ->
                        DropdownMenuItem(
                            text = { Text(source, color = White) },
                            onClick = {
                                selectedSource = source
                                sourceExpanded = false
                            }
                        )
                    }
                }
            }
            
            // Description (Optional)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Zinc800
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (amt != null && amt > 0) {
                        viewModel.addExpense(amt, selectedCategory, selectedSource, description)
                        Toast.makeText(context, "Expense Added", Toast.LENGTH_SHORT).show()
                        onBack()
                    } else {
                        Toast.makeText(context, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Add Record")
                }
            }
        }
    }
}
