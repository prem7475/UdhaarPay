package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
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
fun InsuranceProductsScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val insuranceType = remember { mutableStateOf<String?>(null) }
    val selectedPlan = remember { mutableStateOf<InsurancePlan?>(null) }
    val sumAssured = remember { mutableStateOf("") }
    val tenure = remember { mutableStateOf(10) }
    val monthlyPremium = remember { mutableStateOf(0.0) }
    val remarks = remember { mutableStateOf("") }

    val errorMessage = error
    var showErrorDialog by remember { mutableStateOf(errorMessage != null) }

    LaunchedEffect(errorMessage) {
        showErrorDialog = errorMessage != null
    }

    LaunchedEffect(selectedPlan.value, sumAssured.value, tenure.value) {
        if (selectedPlan.value != null && sumAssured.value.isNotEmpty()) {
            val amount = sumAssured.value.toDoubleOrNull() ?: 0.0
            monthlyPremium.value = (amount / 100000) * selectedPlan.value!!.premiumRate * tenure.value / 12
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
        CommonComponents.LoadingDialog(message = "Processing insurance plan...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonComponents.PremiumTopAppBar(
            title = "Insurance Products",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Insurance Type
            item {
                Text(
                    text = "Insurance Type",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                InsuranceTypeSelectionGrid(
                    selectedType = insuranceType.value,
                    onTypeSelected = { insuranceType.value = it }
                )
            }

            // Plan Selection
            if (insuranceType.value != null) {
                item {
                    Text(
                        text = "Select Plan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InsurancePlanSelectionList(
                        insuranceType = insuranceType.value ?: "",
                        selectedPlan = selectedPlan.value,
                        onPlanSelected = { selectedPlan.value = it }
                    )
                }
            }

            // Sum Assured
            if (selectedPlan.value != null) {
                item {
                    Text(
                        text = "Sum Assured (₹)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CommonComponents.PremiumTextField(
                        value = sumAssured.value,
                        onValueChange = {
                            if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                sumAssured.value = it
                            }
                        },
                        label = "Enter sum assured",
                        leadingIcon = Icons.Default.Shield,
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            // Tenure
            if (selectedPlan.value != null && sumAssured.value.isNotEmpty()) {
                item {
                    Text(
                        text = "Policy Tenure (Years)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InsuranceTenureSlider(
                        tenure = tenure.value,
                        onTenureChanged = { tenure.value = it }
                    )
                }
            }

            // Plan Details
            if (selectedPlan.value != null) {
                item {
                    InsurancePlanDetailsCard(
                        plan = selectedPlan.value!!,
                        sumAssured = sumAssured.value.toDoubleOrNull() ?: 0.0,
                        monthlyPremium = monthlyPremium.value,
                        tenure = tenure.value
                    )
                }
            }

            // Remarks
            if (selectedPlan.value != null && sumAssured.value.isNotEmpty()) {
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
            if (selectedPlan.value != null && sumAssured.value.isNotEmpty()) {
                item {
                    CommonComponents.PremiumButton(
                        text = "Apply for Insurance",
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
private fun InsuranceTypeSelectionGrid(
    selectedType: String?,
    onTypeSelected: (String) -> Unit
) {
    val types = listOf("Life Insurance", "Health Insurance", "Travel Insurance", "Term Life")

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        types.forEach { type ->
            InsuranceTypeCard(
                type = type,
                isSelected = selectedType == type,
                onClick = { onTypeSelected(type) }
            )
        }
    }
}

@Composable
private fun InsuranceTypeCard(
    type: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) AccentOrange else DarkCard
            )
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = type,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) DarkBackground else TextPrimary
        )
    }
}

@Composable
private fun InsurancePlanSelectionList(
    insuranceType: String,
    selectedPlan: InsurancePlan?,
    onPlanSelected: (InsurancePlan) -> Unit
) {
    val plans = getInsurancePlans(insuranceType)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        plans.forEach { plan ->
            InsurancePlanCard(
                plan = plan,
                isSelected = selectedPlan?.id == plan.id,
                onClick = { onPlanSelected(plan) }
            )
        }
    }
}

@Composable
private fun InsurancePlanCard(
    plan: InsurancePlan,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) AccentOrange.copy(alpha = 0.2f) else DarkCard
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
                Text(
                    text = plan.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Rate: ${plan.premiumRate}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = plan.benefits,
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Waiting Period: ${plan.waitingPeriod} days",
                fontSize = 10.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun InsuranceTenureSlider(
    tenure: Int,
    onTenureChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Slider(
            value = tenure.toFloat(),
            onValueChange = { onTenureChanged(it.toInt()) },
            valueRange = 5f..30f,
            steps = 24,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "5 years",
                fontSize = 11.sp,
                color = TextTertiary
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AccentOrange)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$tenure years",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBackground
                )
            }

            Text(
                text = "30 years",
                fontSize = 11.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun InsurancePlanDetailsCard(
    plan: InsurancePlan,
    sumAssured: Double,
    monthlyPremium: Double,
    tenure: Int
) {
    val totalPremium = monthlyPremium * 12 * tenure

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color = CardGradient3Start)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Insurance Summary",
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
                        text = "Sum Assured",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${String.format("%.0f", sumAssured)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Policy Tenure: $tenure years",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Waiting Period: ${plan.waitingPeriod} days",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = TextSecondary.copy(alpha = 0.2f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Monthly Premium",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${String.format("%.0f", monthlyPremium)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Premium ($tenure years)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${String.format("%.0f", totalPremium)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentOrange.copy(alpha = 0.2f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Benefits: ${plan.benefits}",
                    fontSize = 10.sp,
                    color = TextTertiary
                )
            }
        }
    }
}

data class InsurancePlan(
    val id: String,
    val name: String,
    val benefits: String,
    val premiumRate: Double,  // percentage per 1L sum assured per year
    val waitingPeriod: Int    // days
)

private fun getInsurancePlans(insuranceType: String): List<InsurancePlan> {
    return when (insuranceType) {
        "Life Insurance" -> listOf(
            InsurancePlan("li1", "Basic Life Cover", "Death benefit, maturity benefit", 0.45, 90),
            InsurancePlan("li2", "Premium Life", "Death + Disability + Accident", 0.65, 60),
            InsurancePlan("li3", "Family Shield", "Family coverage, loan on policy", 0.55, 75)
        )
        "Health Insurance" -> listOf(
            InsurancePlan("hi1", "Basic Health", "Hospital expenses, outpatient", 0.85, 30),
            InsurancePlan("hi2", "Family Health", "Family coverage, dental", 1.1, 30),
            InsurancePlan("hi3", "Premium Health", "Critical illness, maternity", 1.5, 30)
        )
        "Travel Insurance" -> listOf(
            InsurancePlan("ti1", "Domestic Travel", "Trip cancellation, baggage", 0.2, 0),
            InsurancePlan("ti2", "International", "Visa rejection, emergency medical", 0.35, 0),
            InsurancePlan("ti3", "Adventure Travel", "Adventure sports coverage", 0.5, 0)
        )
        "Term Life" -> listOf(
            InsurancePlan("tl1", "20 Year Term", "Pure death benefit protection", 0.15, 90),
            InsurancePlan("tl2", "30 Year Term", "Extended coverage with benefits", 0.25, 90),
            InsurancePlan("tl3", "40 Year Term", "Lifetime coverage available", 0.35, 90)
        )
        else -> emptyList()
    }
}
