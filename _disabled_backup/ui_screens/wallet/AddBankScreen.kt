package com.example.udhaarpay.ui.screens.wallet

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.BankViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBankScreen(
    onBack: () -> Unit,
    viewModel: BankViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedBank by remember { mutableStateOf("") }
    
    val popularBanks = listOf(
        "State Bank of India",
        "HDFC Bank",
        "ICICI Bank",
        "Axis Bank",
        "Kotak Mahindra Bank",
        "Punjab National Bank",
        "Bank of Baroda",
        "Canara Bank",
        "Union Bank of India"
    )

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            SmallTopAppBar(
                title = { Text("Select Bank", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = PureBlack)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(popularBanks) { bank ->
                BankListItem(
                    bankName = bank,
                    onClick = {
                        selectedBank = bank
                        showBottomSheet = true
                    }
                )
                Divider(color = Zinc800, thickness = 0.5.dp)
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                containerColor = DarkZinc
            ) {
                AddBankForm(
                    bankName = selectedBank,
                    onLink = { holder, number, ifsc ->
                        viewModel.addBankAccount(selectedBank, holder, number, ifsc)
                        Toast.makeText(context, "Bank linked locally (mock)", Toast.LENGTH_SHORT).show()
                        showBottomSheet = false
                        onBack()
                    }
                )
            }
        }
    }
}

@Composable
fun BankListItem(bankName: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Zinc800, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Zinc400)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(bankName, color = White, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun AddBankForm(
    bankName: String,
    onLink: (String, String, String) -> Unit
) {
    var holderName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var ifsc by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .padding(20.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Link $bankName", style = MaterialTheme.typography.titleLarge, color = White)
        
        OutlinedTextField(
            value = holderName,
            onValueChange = { holderName = it },
            label = { Text("Account Holder Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Zinc800,
                cursorColor = PrimaryBlue,
                focusedLabelColor = PrimaryBlue,
                unfocusedLabelColor = Zinc400
            )
        )
        
        OutlinedTextField(
            value = accountNumber,
            onValueChange = { accountNumber = it },
            label = { Text("Account Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Zinc800,
                cursorColor = PrimaryBlue,
                focusedLabelColor = PrimaryBlue,
                unfocusedLabelColor = Zinc400
            )
        )
        
        OutlinedTextField(
            value = ifsc,
            onValueChange = { ifsc = it },
            label = { Text("IFSC Code") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Zinc800,
                cursorColor = PrimaryBlue,
                focusedLabelColor = PrimaryBlue,
                unfocusedLabelColor = Zinc400
            )
        )
        
        Button(
            onClick = { onLink(holderName, accountNumber, ifsc) },
            enabled = accountNumber.length > 9 && holderName.isNotEmpty() && ifsc.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                disabledContainerColor = Zinc800
            )
        ) {
            Text("Link Account", color = if (accountNumber.length > 9 && holderName.isNotEmpty() && ifsc.isNotEmpty()) White else Zinc400)
        }
    }
}
