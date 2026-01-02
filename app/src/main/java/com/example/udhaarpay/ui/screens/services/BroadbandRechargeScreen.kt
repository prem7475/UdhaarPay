package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Brush
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
fun BroadbandRechargeScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val accountNumber = remember { mutableStateOf("") }
    val selectedProvider = remember { mutableStateOf<String?>(null) }
    val selectedPlan = remember { mutableStateOf<BroadbandPlan?>(null) }
    val amount = remember { mutableStateOf("") }
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
        CommonComponents.LoadingDialog(message = "Processing recharge...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonComponents.PremiumTopAppBar(
            title = "Broadband Recharge",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Account Number Input
            item {
                Text(
                    text = "Account Number",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CommonComponents.PremiumTextField(
                    value = accountNumber.value,
                    onValueChange = {
                        if (it.length <= 12) accountNumber.value = it
                    },
                    label = "Enter account number",
                    leadingIcon = Icons.Default.Router,
                    keyboardType = KeyboardType.Number,
                    enabled = selectedPlan.value == null
                )
            }

            // Provider Selection
            if (accountNumber.value.length >= 8) {
                item {
                    Text(
                        text = "Select Provider",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BroadbandProviderGrid(
                        selectedProvider = selectedProvider.value,
                        onProviderSelected = { selectedProvider.value = it }
                    )
                }
            }

            // Plan Selection
            if (selectedProvider.value != null) {
                item {
                    Text(
                        text = "Select Plan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BroadbandPlanList(
                        provider = selectedProvider.value ?: "",
                        selectedPlan = selectedPlan.value,
                        onPlanSelected = {
                            selectedPlan.value = it
                            amount.value = it.price.toString()
                        }
                    )
                }
            }

            // Amount Display
            if (selectedPlan.value != null) {
                item {
                    BroadbandAmountCard(
                        plan = selectedPlan.value!!,
                        amount = amount.value.toDoubleOrNull() ?: 0.0
                    )
                }
            }

            // Remarks
            if (selectedPlan.value != null) {
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
            if (selectedPlan.value != null) {
                item {
                    CommonComponents.PremiumButton(
                        text = "Proceed to Payment",
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
private fun BroadbandProviderGrid(
    selectedProvider: String?,
    onProviderSelected: (String) -> Unit
) {
    val providers = listOf("Jio Fiber", "Airtel Broadband", "BSNL Broadband", "VI Broadband")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        providers.forEach { provider ->
            BroadbandProviderCard(
                name = provider,
                isSelected = selectedProvider == provider,
                onClick = { onProviderSelected(provider) },
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
            )
        }
    }
}

@Composable
private fun BroadbandProviderCard(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) NeonOrange else DarkCard)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = name,
                tint = if (isSelected) DarkBackground else NeonOrange,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name.split(" ")[0],
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) DarkBackground else TextPrimary
            )
        }
    }
}

@Composable
private fun BroadbandPlanList(
    provider: String,
    selectedPlan: BroadbandPlan?,
    onPlanSelected: (BroadbandPlan) -> Unit
) {
    val plans = getBroadbandPlans(provider)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        plans.forEach { plan ->
            BroadbandPlanCard(
                plan = plan,
                isSelected = selectedPlan?.id == plan.id,
                onClick = { onPlanSelected(plan) }
            )
        }
    }
}

@Composable
private fun BroadbandPlanCard(
    plan: BroadbandPlan,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) NeonOrange.copy(alpha = 0.2f) else DarkCard
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
                    text = plan.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${plan.speed} Mbps • ${plan.validity} month(s)",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                if (plan.dataLimit.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plan.dataLimit,
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                }
            }

            Text(
                text = "₹${plan.price}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NeonOrange
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "Selected",
                tint = NeonOrange,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
            )
        }
    }
}

@Composable
private fun BroadbandAmountCard(
    plan: BroadbandPlan,
    amount: Double
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(listOf(CardGradient2Start, CardGradient2End))
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Recharge Details",
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
                        text = "Plan",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = plan.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Speed",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${plan.speed} Mbps",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Valid for ${plan.validity} month(s)",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = TextSecondary.copy(alpha = 0.2f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Amount",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )

                Text(
                    text = "₹$amount",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonOrange
                )
            }
        }
    }
}

data class BroadbandPlan(
    val id: String,
    val name: String,
    val speed: Int,           // Mbps
    val price: Double,
    val validity: Int,        // months
    val dataLimit: String = "" // "Unlimited" or "1TB/month"
)

private fun getBroadbandPlans(provider: String): List<BroadbandPlan> {
    return when (provider) {
        "Jio Fiber" -> listOf(
            BroadbandPlan("jf1", "Basic", 30, 599.0, 1, "Fair Usage Policy"),
            BroadbandPlan("jf2", "Standard", 100, 899.0, 1, "Unlimited Data"),
            BroadbandPlan("jf3", "Premium", 300, 1199.0, 1, "Unlimited Data + TV"),
            BroadbandPlan("jf4", "Yearly", 100, 9999.0, 12, "Unlimited Data")
        )
        "Airtel Broadband" -> listOf(
            BroadbandPlan("ab1", "Basic", 50, 649.0, 1, "Fair Usage"),
            BroadbandPlan("ab2", "Premium", 100, 999.0, 1, "Unlimited Data"),
            BroadbandPlan("ab3", "Ultimate", 300, 1499.0, 1, "Unlimited + TV"),
            BroadbandPlan("ab4", "Annual", 100, 8999.0, 12, "Unlimited Data")
        )
        "BSNL Broadband" -> listOf(
            BroadbandPlan("bb1", "Economy", 20, 399.0, 1, "Fair Usage"),
            BroadbandPlan("bb2", "Standard", 60, 749.0, 1, "Fair Usage"),
            BroadbandPlan("bb3", "Premium", 100, 999.0, 1, "Unlimited"),
            BroadbandPlan("bb4", "Yearly", 60, 7499.0, 12, "Unlimited")
        )
        "VI Broadband" -> listOf(
            BroadbandPlan("vb1", "Basic", 40, 549.0, 1, "Fair Usage"),
            BroadbandPlan("vb2", "Standard", 100, 899.0, 1, "Unlimited"),
            BroadbandPlan("vb3", "Premium", 200, 1199.0, 1, "Unlimited + TV"),
            BroadbandPlan("vb4", "Annual", 100, 8799.0, 12, "Unlimited")
        )
        else -> emptyList()
    }
}
