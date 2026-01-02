package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Phone
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
fun MobileRechargeScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val phoneNumber = remember { mutableStateOf("") }
    val selectedOperator = remember { mutableStateOf<String?>(null) }
    val selectedPlan = remember { mutableStateOf<MobilePlan?>(null) }
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
        // Top App Bar
        CommonComponents.PremiumTopAppBar(
            title = "Mobile Recharge",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Phone Number Input
            item {
                Text(
                    text = "Mobile Number",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CommonComponents.PremiumTextField(
                    value = phoneNumber.value,
                    onValueChange = {
                        if (it.length <= 10) phoneNumber.value = it
                    },
                    label = "Enter 10-digit number",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = NeonOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    keyboardType = KeyboardType.Number,
                    enabled = selectedPlan.value == null
                )
            }

            // Operator Selection
            item {
                Text(
                    text = "Select Operator",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OperatorSelectionGrid(
                    selectedOperator = selectedOperator.value,
                    onOperatorSelected = { selectedOperator.value = it },
                    enabled = phoneNumber.value.length == 10
                )
            }

            // Plan Selection
            if (selectedOperator.value != null) {
                item {
                    Text(
                        text = "Select Plan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PlanSelectionList(
                        operator = selectedOperator.value ?: "",
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
                    AmountDisplayCard(
                        amount = amount.value.toDoubleOrNull() ?: 0.0,
                        planName = selectedPlan.value?.name ?: "",
                        validity = selectedPlan.value?.validity ?: ""
                    )
                }
            }

            // Remarks (Optional)
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
                    singleLine = false,
                    enabled = selectedPlan.value != null
                )
            }

            // Proceed Button
            if (selectedPlan.value != null) {
                item {
                    CommonComponents.PremiumButton(
                        text = "Proceed to Payment",
                        onClick = {
                            // Navigate to payment or process recharge
                        },
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
private fun OperatorSelectionGrid(
    selectedOperator: String?,
    onOperatorSelected: (String) -> Unit,
    enabled: Boolean
) {
    val operators = listOf("Jio", "Airtel", "VI", "BSNL")

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        operators.forEach { operator ->
            OperatorCard(
                name = operator,
                isSelected = selectedOperator == operator,
                onClick = { onOperatorSelected(operator) },
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                enabled = enabled
            )
        }
    }
}

@Composable
private fun OperatorCard(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val backgroundColor = when {
        isSelected -> NeonOrange
        enabled -> DarkCard
        else -> DarkCard.copy(alpha = 0.5f)
    }

    val textColor = when {
        isSelected -> DarkBackground
        else -> TextPrimary
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun PlanSelectionList(
    operator: String,
    selectedPlan: MobilePlan?,
    onPlanSelected: (MobilePlan) -> Unit
) {
    val plans = getMobileOperatorPlans(operator)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        plans.forEach { plan ->
            PlanCard(
                plan = plan,
                isSelected = selectedPlan?.id == plan.id,
                onClick = { onPlanSelected(plan) }
            )
        }
    }
}

@Composable
private fun PlanCard(
    plan: MobilePlan,
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
            modifier = Modifier
                .fillMaxWidth(),
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
                    text = "Validity: ${plan.validity}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                if (plan.benefits.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plan.benefits,
                        fontSize = 11.sp,
                        color = TextTertiary,
                        maxLines = 1
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
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Selected",
                tint = NeonOrange,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun AmountDisplayCard(
    amount: Double,
    planName: String,
    validity: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(listOf(CardGradient1Start, CardGradient1End))
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
                        text = planName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Amount",
                        fontSize = 12.sp,
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Valid for $validity",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

data class MobilePlan(
    val id: String,
    val name: String,
    val price: Double,
    val validity: String,
    val benefits: String = ""
)

private fun getMobileOperatorPlans(operator: String): List<MobilePlan> {
    return when (operator) {
        "Jio" -> listOf(
            MobilePlan("jio1", "Jio Lite", 98.0, "28 Days", "1.5GB/day + Calls"),
            MobilePlan("jio2", "Jio Premium", 249.0, "28 Days", "2GB/day + Calls"),
            MobilePlan("jio3", "Jio Max", 499.0, "28 Days", "3.5GB/day + Calls"),
            MobilePlan("jio4", "Annual Plan", 2499.0, "365 Days", "2GB/day + Calls")
        )
        "Airtel" -> listOf(
            MobilePlan("air1", "Airtel Basic", 79.0, "28 Days", "1GB/day + Calls"),
            MobilePlan("air2", "Airtel Plus", 199.0, "28 Days", "2GB/day + Calls"),
            MobilePlan("air3", "Airtel Prime", 449.0, "28 Days", "3GB/day + Calls"),
            MobilePlan("air4", "Annual Plan", 2399.0, "365 Days", "2GB/day + Calls")
        )
        "VI" -> listOf(
            MobilePlan("vi1", "VI Lite", 69.0, "28 Days", "0.5GB/day + Calls"),
            MobilePlan("vi2", "VI Smart", 189.0, "28 Days", "1.5GB/day + Calls"),
            MobilePlan("vi3", "VI Premium", 399.0, "28 Days", "2.5GB/day + Calls"),
            MobilePlan("vi4", "Annual Plan", 1999.0, "365 Days", "1.5GB/day + Calls")
        )
        "BSNL" -> listOf(
            MobilePlan("bsnl1", "BSNL Basic", 49.0, "28 Days", "1GB/day + Calls"),
            MobilePlan("bsnl2", "BSNL Standard", 149.0, "28 Days", "2GB/day + Calls"),
            MobilePlan("bsnl3", "BSNL Premium", 349.0, "28 Days", "3GB/day + Calls"),
            MobilePlan("bsnl4", "Annual Plan", 1599.0, "365 Days", "1.5GB/day + Calls")
        )
        else -> emptyList()
    }
}
