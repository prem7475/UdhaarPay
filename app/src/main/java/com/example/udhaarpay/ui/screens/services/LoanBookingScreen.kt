package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.ui.components.CommonComponents
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.ServiceViewModel

@Composable
fun LoanBookingScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val loanType = remember { mutableStateOf<LoanType?>(null) }
    val loanAmount = remember { mutableStateOf("") }
    val tenure = remember { mutableStateOf(12) }
    val selectedBank = remember { mutableStateOf<Bank?>(null) }
    val monthlyEmi = remember { mutableStateOf(0.0) }
    val totalAmount = remember { mutableStateOf(0.0) }
    val remarks = remember { mutableStateOf("") }

    val errorMessage = error
    var showErrorDialog by remember { mutableStateOf(errorMessage != null) }

    LaunchedEffect(errorMessage) {
        showErrorDialog = errorMessage != null
    }

    LaunchedEffect(loanAmount.value, tenure.value, selectedBank.value) {
        val amount = loanAmount.value.toDoubleOrNull() ?: 0.0
        val bank = selectedBank.value
        if (amount > 0 && bank != null) {
            val monthlyRate = bank.interestRate / 100 / 12
            monthlyEmi.value = (amount * monthlyRate * Math.pow(1 + monthlyRate, tenure.value.toDouble())) /
                    (Math.pow(1 + monthlyRate, tenure.value.toDouble()) - 1)
            totalAmount.value = monthlyEmi.value * tenure.value
        }
    }

    if (showErrorDialog && errorMessage != null) {
        CommonComponents.ErrorDialog(
            title = "Error",
            message = errorMessage,
            onDismiss = {
                showErrorDialog = false
                viewModel.clearError()
            }
        )
    }

    if (isLoading) {
        CommonComponents.LoadingDialog(message = "Processing loan application...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonComponents.PremiumTopAppBar(
            title = "Apply for Loan",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Loan Type
            item {
                Text(
                    text = "Loan Type",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LoanTypeSelectionGrid(
                    selectedType = loanType.value,
                    onTypeSelected = { loanType.value = it }
                )
            }

            // Loan Amount
            item {
                Text(
                    text = "Loan Amount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CommonComponents.PremiumTextField(
                    value = loanAmount.value,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null) {
                            loanAmount.value = it
                        }
                    },
                    label = "Enter amount (₹)",
                    leadingIcon = Icons.Default.AttachMoney,
                    keyboardType = KeyboardType.Number
                )
            }

            // Tenure
            item {
                Text(
                    text = "Loan Tenure (Months)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                TenureSlider(
                    tenure = tenure.value,
                    onTenureChanged = { tenure.value = it }
                )
            }

            // Bank Selection
            if (loanType.value != null && loanAmount.value.isNotEmpty()) {
                item {
                    Text(
                        text = "Select Bank",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BankSelectionList(
                        loanType = loanType.value!!,
                        selectedBank = selectedBank.value,
                        onBankSelected = { selectedBank.value = it }
                    )
                }
            }

            // Loan Summary
            if (selectedBank.value != null && loanAmount.value.isNotEmpty()) {
                item {
                    LoanSummaryCard(
                        loanType = loanType.value!!,
                        loanAmount = loanAmount.value.toDoubleOrNull() ?: 0.0,
                        bank = selectedBank.value!!,
                        tenure = tenure.value,
                        monthlyEmi = monthlyEmi.value,
                        totalAmount = totalAmount.value
                    )
                }
            }

            // Remarks
            if (selectedBank.value != null) {
                item {
                    Text(
                        text = "Remarks (Optional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CommonComponents.PremiumTextField(
                        value = remarks.value,
                        onValueChange = { remarks.value = it },
                        label = "Add notes",
                        singleLine = false
                    )
                }
            }

            // Proceed Button
            if (selectedBank.value != null) {
                item {
                    CommonComponents.PremiumButton(
                        text = "Apply for Loan",
                        onClick = { },
                        isLoading = isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun LoanTypeSelectionGrid(
    selectedType: LoanType?,
    onTypeSelected: (LoanType) -> Unit
) {
    val types = listOf(
        LoanType("1", "Personal Loan"),
        LoanType("2", "Home Loan"),
        LoanType("3", "Auto Loan"),
        LoanType("4", "Education Loan")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        types.forEach { type ->
            LoanTypeCard(
                type = type,
                isSelected = selectedType?.id == type.id,
                onClick = { onTypeSelected(type) }
            )
        }
    }
}

@Composable
private fun LoanTypeCard(
    type: LoanType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) ErrorRed.copy(alpha = 0.2f) else DarkCard
            )
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = type.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) ErrorRed else TextPrimary
        )
    }
}

@Composable
private fun TenureSlider(
    tenure: Int,
    onTenureChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Slider(
            value = tenure.toFloat(),
            onValueChange = { onTenureChanged(it.toInt()) },
            valueRange = 6f..60f,
            steps = 53,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "6 months",
                fontSize = 11.sp,
                color = TextTertiary
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(ErrorRed)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$tenure months",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBackground
                )
            }

            Text(
                text = "60 months",
                fontSize = 11.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun BankSelectionList(
    loanType: LoanType,
    selectedBank: Bank?,
    onBankSelected: (Bank) -> Unit
) {
    val banks = getBanksForLoanType(loanType.name)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        banks.forEach { bank ->
            BankCard(
                bank = bank,
                isSelected = selectedBank?.id == bank.id,
                onClick = { onBankSelected(bank) }
            )
        }
    }
}

@Composable
private fun BankCard(
    bank: Bank,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) ErrorRed.copy(alpha = 0.2f) else DarkCard
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bank.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Interest: ${bank.interestRate}% p.a. | Processing: ${bank.processingFee}%",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) ErrorRed else DarkCard)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${bank.maxAmount}L",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) DarkBackground else TextPrimary
                )
            }
        }
    }
}

@Composable
private fun LoanSummaryCard(
    loanType: LoanType,
    loanAmount: Double,
    bank: Bank,
    tenure: Int,
    monthlyEmi: Double,
    totalAmount: Double
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color = CardGradient2Start)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Loan Estimate",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Loan Type",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = loanType.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Bank",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = bank.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Loan Amount: ₹${String.format("%.2f", loanAmount)}",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tenure: $tenure months (${tenure / 12} years ${tenure % 12} months)",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Interest Rate: ${bank.interestRate}% p.a.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = TextSecondary.copy(alpha = 0.2f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Monthly EMI",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${String.format("%.0f", monthlyEmi)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Total Amount",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${String.format("%.0f", totalAmount)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

data class LoanType(
    val id: String,
    val name: String
)

data class Bank(
    val id: String,
    val name: String,
    val interestRate: Double,
    val processingFee: Double,
    val maxAmount: Int
)

private fun getBanksForLoanType(loanType: String): List<Bank> {
    return listOf(
        Bank("b1", "HDFC Bank", 9.5, 1.0, 50),
        Bank("b2", "ICICI Bank", 9.8, 1.2, 45),
        Bank("b3", "SBI", 8.9, 0.8, 60),
        Bank("b4", "Axis Bank", 9.7, 1.1, 40)
    )
}
