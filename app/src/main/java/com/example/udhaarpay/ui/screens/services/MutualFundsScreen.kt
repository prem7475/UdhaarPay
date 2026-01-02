package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Info
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
fun MutualFundsScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val investmentAmount = remember { mutableStateOf("") }
    val selectedFund = remember { mutableStateOf<MutualFund?>(null) }
    val investmentType = remember { mutableStateOf("Lumpsum") }
    val remarks = remember { mutableStateOf("") }

    val errorMessage = error
    var showErrorDialog by remember { mutableStateOf(errorMessage != null) }

    LaunchedEffect(errorMessage) {
        showErrorDialog = errorMessage != null
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
        CommonComponents.LoadingDialog(message = "Processing investment...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonComponents.PremiumTopAppBar(
            title = "Invest in Mutual Funds",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Investment Type
            item {
                Text(
                    text = "Investment Type",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                InvestmentTypeSelector(
                    selectedType = investmentType.value,
                    onTypeSelected = { investmentType.value = it }
                )
            }

            // Amount Input
            item {
                Text(
                    text = "Investment Amount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CommonComponents.PremiumTextField(
                    value = investmentAmount.value,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null) {
                            investmentAmount.value = it
                        }
                    },
                    label = "Enter amount (₹)",
                    leadingIcon = Icons.Default.TrendingUp,
                    keyboardType = KeyboardType.Number
                )
            }

            // Fund Selection
            item {
                Text(
                    text = "Select Mutual Fund",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                MutualFundSelectionList(
                    selectedFund = selectedFund.value,
                    onFundSelected = { selectedFund.value = it }
                )
            }

            // Fund Details
            if (selectedFund.value != null) {
                item {
                    MutualFundDetailsCard(
                        fund = selectedFund.value!!,
                        investmentAmount = investmentAmount.value.toDoubleOrNull() ?: 0.0
                    )
                }
            }

            // Remarks
            if (selectedFund.value != null) {
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
            if (selectedFund.value != null && investmentAmount.value.isNotEmpty()) {
                item {
                    CommonComponents.PremiumButton(
                        text = "Proceed to Investment",
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
private fun InvestmentTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("Lumpsum", "SIP").forEach { type ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedType == type) SuccessGreen else DarkCard)
                    .clickable { onTypeSelected(type) }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedType == type) DarkBackground else TextPrimary
                )
            }
        }
    }
}

@Composable
private fun MutualFundSelectionList(
    selectedFund: MutualFund?,
    onFundSelected: (MutualFund) -> Unit
) {
    val funds = listOf(
        MutualFund("mf1", "Growth Plus 100", "Large Cap", 125.45, 18.5, "Equity"),
        MutualFund("mf2", "Balanced Shield", "Balanced", 95.30, 12.3, "Hybrid"),
        MutualFund("mf3", "Dividend Gold", "Large Cap", 138.75, 10.2, "Equity"),
        MutualFund("mf4", "Fixed Security", "Debt", 45.60, 6.5, "Debt")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        funds.forEach { fund ->
            MutualFundCard(
                fund = fund,
                isSelected = selectedFund?.id == fund.id,
                onClick = { onFundSelected(fund) }
            )
        }
    }
}

@Composable
private fun MutualFundCard(
    fund: MutualFund,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) SuccessGreen.copy(alpha = 0.2f) else DarkCard
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = fund.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = fund.category,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "NAV",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${fund.nav}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "1Y Return: ${fund.oneYearReturn}%",
                    fontSize = 12.sp,
                    color = if (fund.oneYearReturn > 10) SuccessGreen else TextSecondary
                )

                Text(
                    text = fund.type,
                    fontSize = 11.sp,
                    color = TextTertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MutualFundDetailsCard(
    fund: MutualFund,
    investmentAmount: Double
) {
    val projectedValue = investmentAmount * (1 + fund.oneYearReturn / 100)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color = CardGradient1Start)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Investment Summary",
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
                        text = "Fund",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = fund.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Expected Return",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${fund.oneYearReturn}% p.a.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (investmentAmount > 0) {
                Text(
                    text = "Investment Amount: ₹${String.format("%.2f", investmentAmount)}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Projected Value (1Y): ₹${String.format("%.2f", projectedValue)}",
                    fontSize = 12.sp,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = TextSecondary.copy(alpha = 0.2f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Note: Returns are not guaranteed. Past performance is not indicative of future results.",
                fontSize = 10.sp,
                color = TextTertiary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class MutualFund(
    val id: String,
    val name: String,
    val category: String,
    val nav: Double,
    val oneYearReturn: Double,
    val type: String
)
