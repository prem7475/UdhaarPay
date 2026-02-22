package com.udhaarpay.app.ui.screens.billpayments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.local.entities.Expense
import com.udhaarpay.app.ui.viewmodel.BankAccountViewModel
import com.udhaarpay.app.ui.screens.common.InAppBrowserDialog
import com.udhaarpay.app.ui.viewmodel.ExpenseViewModel
import java.util.Locale

private data class MockBill(
    val billerName: String,
    val amount: Double,
    val dueDate: String,
    val websiteUrl: String
)

@Composable
fun BillPaymentScreen(
    viewModel: ExpenseViewModel = hiltViewModel(),
    bankAccountViewModel: BankAccountViewModel = hiltViewModel()
) {
    val bankAccounts by bankAccountViewModel.accounts.collectAsState()
    var billType by remember { mutableStateOf("Electricity") }
    var accountNumber by remember { mutableStateOf("") }
    var fetchedBill by remember { mutableStateOf<MockBill?>(null) }
    var showBrowser by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var selectedBankId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(bankAccounts) {
        if (selectedBankId == null) {
            selectedBankId = bankAccounts.firstOrNull()?.accountId
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Bill Payments", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = billType,
            onValueChange = { billType = it },
            label = { Text("Bill Type (Electricity/Water/Gas/Internet/Mobile)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = accountNumber,
            onValueChange = { accountNumber = it },
            label = { Text("Consumer / Account Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = bankAccounts.firstOrNull { it.accountId == selectedBankId }?.bankName ?: "Select Bank",
            onValueChange = {},
            readOnly = true,
            label = { Text("Paying Account") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            bankAccounts.take(3).forEach { account ->
                TextButton(onClick = { selectedBankId = account.accountId }) {
                    Text(account.bankName.take(10))
                }
            }
        }
        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {
                    fetchedBill = getMockBill(billType)
                    message = null
                }
            ) {
                Text("Fetch Bill")
            }
            TextButton(onClick = {
                fetchedBill = null
                accountNumber = ""
                message = null
            }) {
                Text("Reset")
            }
        }

        Spacer(Modifier.height(14.dp))
        fetchedBill?.let { bill ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(bill.billerName, fontWeight = FontWeight.SemiBold)
                    Text("Amount Due: INR ${bill.amount}")
                    Text("Due Date: ${bill.dueDate}")
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = {
                            val selectedBank = bankAccounts.firstOrNull { it.accountId == selectedBankId }
                            viewModel.insert(
                                Expense(
                                    amount = bill.amount,
                                    category = "Bills",
                                    subCategory = billType,
                                    account = "bank",
                                    accountName = selectedBank?.bankName ?: "Default Bank",
                                    description = "$billType bill payment",
                                    date = System.currentTimeMillis(),
                                    month = java.text.SimpleDateFormat("MMM yyyy", Locale.getDefault())
                                        .format(java.util.Date()),
                                    receiptUrl = null,
                                    accountId = selectedBank?.accountId
                                )
                            )
                            message = "Bill payment recorded successfully"
                        }) {
                            Text("Pay Now (Mock)")
                        }
                        TextButton(onClick = { showBrowser = true }) {
                            Text("Pay on Provider Site")
                        }
                    }
                }
            }
        }

        if (!message.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(message ?: "", color = MaterialTheme.colorScheme.primary)
        }
    }

    if (showBrowser && fetchedBill != null) {
        InAppBrowserDialog(
            title = fetchedBill?.billerName ?: "Provider",
            url = fetchedBill?.websiteUrl ?: "https://www.paytm.com/",
            onDismiss = { showBrowser = false }
        )
    }
}

private fun getMockBill(type: String): MockBill {
    return when (type.trim().lowercase(Locale.getDefault())) {
        "electricity" -> MockBill("State Electricity Board", 2500.0, "15 Feb 2026", "https://www.paytm.com/electricity-bill-payment")
        "water" -> MockBill("Municipal Water Supply", 800.0, "25 Feb 2026", "https://www.paytm.com/water-bill-payment")
        "gas" -> MockBill("Gas Utility Provider", 1200.0, "20 Feb 2026", "https://www.paytm.com/piped-gas-bill-payment")
        "internet" -> MockBill("Broadband ISP", 999.0, "28 Feb 2026", "https://www.airtel.in/broadband/")
        "mobile" -> MockBill("Mobile Postpaid", 599.0, "26 Feb 2026", "https://www.airtel.in/postpaid")
        else -> MockBill("Utility Service", 950.0, "22 Feb 2026", "https://www.paytm.com/")
    }
}
