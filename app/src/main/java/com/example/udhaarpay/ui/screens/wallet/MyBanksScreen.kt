package com.example.udhaarpay.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.data.local.entity.BankAccountEntity
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.BankViewModel

@Composable
fun MyBanksScreen(
    onBack: () -> Unit,
    onAddBank: () -> Unit,
    viewModel: BankViewModel = hiltViewModel()
) {
    val bankAccounts by viewModel.bankAccounts.collectAsState()

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            SmallTopAppBar(
                title = { Text("My Bank Accounts", color = White) },
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
                onClick = onAddBank,
                containerColor = PrimaryBlue,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Bank")
            }
        }
    ) { padding ->
        if (bankAccounts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No linked accounts found", color = Zinc400)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(bankAccounts) { account ->
                    BankAccountCard(
                        account = account,
                        onDelete = { viewModel.deleteBankAccount(account) }
                    )
                }
            }
        }
    }
}

@Composable
fun BankAccountCard(
    account: BankAccountEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkZinc),
        border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    account.bankName,
                    color = White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Acct: ****${account.accountNumber.takeLast(4)}",
                    color = Zinc400,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "₹${String.format("%,.2f", account.balance)}",
                    color = PrimaryBlue,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .background(Color.Red.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}
