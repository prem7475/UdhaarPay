package com.udhaarpay.app.ui.screens.billpayments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.udhaarpay.app.ui.screens.common.InAppBrowserDialog
import com.udhaarpay.app.ui.viewmodel.BankAccountViewModel
import com.udhaarpay.app.ui.viewmodel.ExpenseViewModel
import java.util.Locale

private data class RechargePlan(
    val provider: String,
    val price: Double,
    val benefits: String
)

@Composable
fun MobileRechargeScreen(
    viewModel: ExpenseViewModel = hiltViewModel(),
    bankAccountViewModel: BankAccountViewModel = hiltViewModel()
) {
    val bankAccounts by bankAccountViewModel.accounts.collectAsState()
    var phoneNumber by remember { mutableStateOf("") }
    var selectedPlan by remember { mutableStateOf<RechargePlan?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var showBrowser by remember { mutableStateOf(false) }
    var selectedBankId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(bankAccounts) {
        if (selectedBankId == null) {
            selectedBankId = bankAccounts.firstOrNull()?.accountId
        }
    }

    val operator = detectOperator(phoneNumber)
    val plans = getPlans(operator)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Mobile Recharge", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it.filter(Char::isDigit).take(10)
            },
            label = { Text("Mobile Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Text("Detected Operator: $operator", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
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
        Spacer(Modifier.height(12.dp))

        Text("Available Plans", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(plans) { plan ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPlan == plan) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("INR ${plan.price}", fontWeight = FontWeight.Bold)
                            Text(plan.benefits)
                        }
                        Button(onClick = { selectedPlan = plan }) {
                            Text("Select")
                        }
                    }
                }
            }
        }

        selectedPlan?.let { plan ->
            Spacer(Modifier.height(8.dp))
            Text("Selected: INR ${plan.price} | ${plan.provider}")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        if (phoneNumber.length == 10) {
                            val selectedBank = bankAccounts.firstOrNull { it.accountId == selectedBankId }
                            viewModel.insert(
                                Expense(
                                    amount = plan.price,
                                    category = "Recharge",
                                    subCategory = operator,
                                    account = "bank",
                                    accountName = selectedBank?.bankName ?: "Default Bank",
                                    description = "${plan.provider} recharge for $phoneNumber",
                                    date = System.currentTimeMillis(),
                                    month = java.text.SimpleDateFormat("MMM yyyy", Locale.getDefault())
                                        .format(java.util.Date()),
                                    receiptUrl = null,
                                    accountId = selectedBank?.accountId
                                )
                            )
                            message = "Recharge recorded. Ref: RCG${System.currentTimeMillis()}"
                        } else {
                            message = "Enter a valid 10-digit number"
                        }
                    }
                ) {
                    Text("Recharge Now (Mock)")
                }
                TextButton(onClick = { showBrowser = true }) {
                    Text("Recharge on Provider Site")
                }
            }
        }

        if (!message.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(message ?: "", color = MaterialTheme.colorScheme.primary)
        }
    }

    if (showBrowser && selectedPlan != null) {
        InAppBrowserDialog(
            title = "${selectedPlan?.provider ?: "Provider"} Recharge",
            url = providerWebsite(selectedPlan?.provider ?: "jio"),
            onDismiss = { showBrowser = false }
        )
    }
}

private fun detectOperator(number: String): String {
    if (number.length < 2) return "Unknown"
    return when (number.first()) {
        '9' -> "Jio"
        '8' -> "Airtel"
        '7' -> "VI"
        '6' -> "BSNL"
        else -> "Unknown"
    }
}

private fun getPlans(operator: String): List<RechargePlan> {
    return when (operator) {
        "Jio" -> listOf(
            RechargePlan("Jio", 99.0, "2GB/day | 28 days"),
            RechargePlan("Jio", 249.0, "2.5GB/day | 28 days"),
            RechargePlan("Jio", 399.0, "3GB/day | 28 days")
        )
        "Airtel" -> listOf(
            RechargePlan("Airtel", 79.0, "2GB | 28 days"),
            RechargePlan("Airtel", 199.0, "3GB | 28 days"),
            RechargePlan("Airtel", 359.0, "4.5GB | 28 days")
        )
        "VI" -> listOf(
            RechargePlan("VI", 99.0, "2GB | 28 days"),
            RechargePlan("VI", 199.0, "3GB | 28 days"),
            RechargePlan("VI", 399.0, "4GB | 28 days")
        )
        else -> listOf(
            RechargePlan("BSNL", 107.0, "3GB | 35 days"),
            RechargePlan("BSNL", 199.0, "2GB/day | 30 days")
        )
    }
}

private fun providerWebsite(provider: String): String {
    return when (provider.lowercase(Locale.getDefault())) {
        "jio" -> "https://www.jio.com/selfcare/recharge/mobility/"
        "airtel" -> "https://www.airtel.in/recharge-online/"
        "vi" -> "https://www.myvi.in/prepaid/recharge-plans"
        else -> "https://portal2.bsnl.in/myportal/quickrecharge.do"
    }
}
