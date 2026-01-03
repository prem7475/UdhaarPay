package com.example.udhaarpay.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.udhaarpay.data.model.Udhari
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.UdhariViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UdhariScreen(
    onBack: () -> Unit,
    viewModel: UdhariViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Calculate totals
    val totalToReceive = state.records
        .filter { it.type == "GIVEN" && !it.isPaid }
        .sumOf { it.amount }
        
    val totalToPay = state.records
        .filter { it.type == "TAKEN" && !it.isPaid }
        .sumOf { it.amount }

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            SmallTopAppBar(
                title = { Text("Udhari Book", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = PureBlack)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryBlue,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Record")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Totals Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // To Receive Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkZinc),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("To Receive", color = Zinc400, style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "₹${String.format("%.0f", totalToReceive)}",
                            color = Color.Green,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // To Pay Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkZinc),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("To Pay", color = Zinc400, style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "₹${String.format("%.0f", totalToPay)}",
                            color = Color.Red,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // List
            val activeRecords = state.records.filter { !it.isPaid }

            if (activeRecords.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No active records", color = Zinc400)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(activeRecords) { record ->
                        UdhariItem(
                            record = record,
                            onSettle = { viewModel.settleRecord(record.id) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddUdhariDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, amount, type, category, source ->
                    viewModel.addRecord(name, amount, type, category, source)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun UdhariItem(
    record: Udhari,
    onSettle: () -> Unit
) {
    val isGave = record.type == "GIVEN"
    
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkZinc),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon based on type
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(if(isGave) Color.Green.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if(isGave) Icons.Default.ArrowOutward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if(isGave) Color.Green else Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(record.customerName, color = White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        record.type,
                        color = Zinc400,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "• ${SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(record.createdAt))}",
                        color = Zinc400,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${String.format("%.0f", record.amount)}",
                    color = if (isGave) Color.Green else Color.Red,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onSettle,
                    colors = ButtonDefaults.buttonColors(containerColor = Zinc800),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Settle", fontSize = 12.sp, color = White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUdhariDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Double, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("GIVEN") } // GIVEN or TAKEN
    
    // Category Dropdown
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Other") }
    val categories = listOf("Food", "Travel", "Salary", "Rent", "Loan", "Other")

    // Source Dropdown
    var sourceExpanded by remember { mutableStateOf(false) }
    var selectedSource by remember { mutableStateOf("Wallet") }
    val sources = listOf("Wallet", "Bank Account")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkZinc),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Add Record", color = White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Toggle Type
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Zinc800, RoundedCornerShape(8.dp)),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (type == "GIVEN") Color.Green else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { type = "GIVEN" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Given", color = if(type == "GIVEN") PureBlack else White, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (type == "TAKEN") Color.Red else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { type = "TAKEN" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Taken", color = if(type == "TAKEN") PureBlack else White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Person Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Zinc800
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Zinc800
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Dropdown
                androidx.compose.material3.ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = Zinc400) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Zinc800
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    androidx.compose.material3.ExposedDropdownMenu(
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

                Spacer(modifier = Modifier.height(8.dp))

                // Source Dropdown
                androidx.compose.material3.ExposedDropdownMenuBox(
                    expanded = sourceExpanded,
                    onExpandedChange = { sourceExpanded = !sourceExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedSource,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Source") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = Zinc400) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Zinc800
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    androidx.compose.material3.ExposedDropdownMenu(
                        expanded = sourceExpanded,
                        onDismissRequest = { sourceExpanded = false },
                        modifier = Modifier.background(DarkZinc)
                    ) {
                        sources.forEach { source ->
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

                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        val amt = amount.toDoubleOrNull()
                        if (name.isNotEmpty() && amt != null) {
                            onAdd(name, amt, type, selectedCategory, selectedSource)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Record")
                }
            }
        }
    }
}


