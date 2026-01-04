package com.example.udhaarpay.ui.screens.investments

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

@Composable
fun DematScreen() {
    val brokers = listOf(
        "Zerodha", "Groww", "5Paisa", "Upstox", "Angel One", "ICICI Direct", "HDFC Securities", "Motilal Oswal"
    )
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Demat Accounts", "Bonds", "Transactions")
    var activeAccounts by remember { mutableStateOf(listOf<String>()) }
    var showOpenDialog by remember { mutableStateOf(false) }
    var openedAccount by remember { mutableStateOf<String?>(null) }
    var transactionHistory by remember { mutableStateOf(listOf<String>()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { i, title ->
                Tab(
                    selected = selectedTab == i,
                    onClick = { selectedTab = i },
                    text = { Text(title) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        when (selectedTab) {
            0 -> {
                Text("Open a Demat Account", fontWeight = FontWeight.Bold)
                LazyColumn {
                    items(brokers) { broker ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    openedAccount = broker
                                    showOpenDialog = true
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA))
                        ) {
                            Box(Modifier.padding(16.dp)) {
                                Text(broker, fontSize = 18.sp)
                            }
                        }
                    }
                }
                if (showOpenDialog && openedAccount != null) {
                    AlertDialog(
                        onDismissRequest = { showOpenDialog = false },
                        title = { Text("Open Demat Account") },
                        text = { Text("Account opened with ${openedAccount}! Account No: DP${(10000..99999).random()}") },
                        confirmButton = {
                            TextButton(onClick = {
                                activeAccounts = activeAccounts + openedAccount!!
                                transactionHistory = transactionHistory + "Opened account with $openedAccount"
                                showOpenDialog = false
                            }) { Text("OK") }
                        }
                    )
                }
                if (activeAccounts.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text("Active Demat Accounts:", fontWeight = FontWeight.Bold)
                    activeAccounts.forEach { acc ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))
                        ) {
                            Box(Modifier.padding(12.dp)) { Text(acc) }
                        }
                    }
                }
            }
            1 -> {
                Text("Government Bonds", fontWeight = FontWeight.Bold)
                LazyColumn {
                    items(listOf("Sovereign Gold Bond", "RBI Floating Rate Bond", "GOI Savings Bond")) { bond ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
                        ) {
                            Box(Modifier.padding(16.dp)) {
                                Text(bond, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
            2 -> {
                Text("Transaction History", fontWeight = FontWeight.Bold)
                if (transactionHistory.isEmpty()) {
                    Text("No transactions yet.", color = Color.Gray)
                } else {
                    LazyColumn {
                        items(transactionHistory) { tx ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                            ) {
                                Box(Modifier.padding(12.dp)) { Text(tx) }
                            }
                        }
                    }
                }
            }
        }
    }
}
