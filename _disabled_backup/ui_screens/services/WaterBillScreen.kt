package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import com.example.udhaarpay.ui.icons.*
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
fun WaterBillScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val consumerNumber = remember { mutableStateOf("") }
    val selectedCity = remember { mutableStateOf<String?>(null) }
    val selectedProvider = remember { mutableStateOf<String?>(null) }
    val billDetails = remember { mutableStateOf<WaterBill?>(null) }
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
        CommonComponents.LoadingDialog(message = "Fetching water bill...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonComponents.PremiumTopAppBar(
            title = "Water Bill Payment",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Consumer Number Input
            item {
                Text(
                    text = "Consumer Number",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CommonComponents.PremiumTextField(
                    value = consumerNumber.value,
                    onValueChange = {
                        if (it.length <= 10) consumerNumber.value = it
                    },
                    label = "Enter consumer number",
                    leadingIcon = Droplet,
                    keyboardType = KeyboardType.Number,
                    enabled = selectedProvider.value == null
                )
            }

            // City Selection
            if (consumerNumber.value.length >= 6) {
                item {
                    Text(
                        text = "Select City",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CitySelectionDropdown(
                        selectedCity = selectedCity.value,
                        onCitySelected = { selectedCity.value = it }
                    )
                }
            }

            // Water Provider Selection
            if (selectedCity.value != null) {
                item {
                    Text(
                        text = "Water Board",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    WaterProviderGrid(
                        city = selectedCity.value ?: "",
                        selectedProvider = selectedProvider.value,
                        onProviderSelected = { provider ->
                            selectedProvider.value = provider
                            // Simulate fetching bill
                            billDetails.value = WaterBill(
                                id = "water_${System.currentTimeMillis()}",
                                city = selectedCity.value ?: "",
                                provider = provider,
                                consumerNumber = consumerNumber.value,
                                outstandingAmount = (300..2500).random().toDouble(),
                                dueDate = "20/12/2025",
                                consumerName = "Priya Singh",
                                meterReading = (50000..99999).random(),
                                connectionType = "Residential"
                            )
                        }
                    )
                }
            }

            // Bill Details
            if (billDetails.value != null) {
                item {
                    WaterBillDetailsCard(bill = billDetails.value!!)
                }
            }

            // Remarks
            if (selectedProvider.value != null) {
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
            if (billDetails.value != null) {
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
private fun CitySelectionDropdown(
    selectedCity: String?,
    onCitySelected: (String) -> Unit
) {
    val cities = listOf(
        "Bangalore", "Mumbai", "Delhi", "Chennai", "Hyderabad",
        "Pune", "Ahmedabad", "Kolkata", "Jaipur", "Lucknow",
        "Chandigarh", "Indore", "Nagpur", "Bhopal", "Visakhapatnam"
    )

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = DarkCard,
                contentColor = TextPrimary
            ),
            border = null,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = selectedCity ?: "Select City",
                fontSize = 14.sp,
                color = if (selectedCity != null) TextPrimary else TextSecondary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .background(DarkCard)
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = city,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        if (selectedCity == city) NeonOrange.copy(alpha = 0.2f) else DarkCard
                    )
                )
            }
        }
    }
}

@Composable
private fun WaterProviderGrid(
    city: String,
    selectedProvider: String?,
    onProviderSelected: (String) -> Unit
) {
    val providers = getCityWaterProviders(city)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        providers.forEach { provider ->
            WaterProviderCard(
                name = provider,
                isSelected = selectedProvider == provider,
                onClick = { onProviderSelected(provider) }
            )
        }
    }
}

@Composable
private fun WaterProviderCard(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) AccentBlue.copy(alpha = 0.2f) else DarkCard
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Droplet,
                contentDescription = name,
                tint = if (isSelected) AccentBlue else TextSecondary,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Text(
                    text = "✓",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlue
                )
            }
        }
    }
}

@Composable
private fun WaterBillDetailsCard(bill: WaterBill) {
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
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Bill Details",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Consumer Name",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = bill.consumerName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Type",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = bill.connectionType,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentBlue
                    )
                }
            }

            Divider(color = TextSecondary.copy(alpha = 0.2f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Meter Reading",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${bill.meterReading} L",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Due Date",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = bill.dueDate,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }

            Divider(color = TextSecondary.copy(alpha = 0.2f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Amount Due",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )

                Text(
                    text = "₹${bill.outstandingAmount.toLong()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen
                )
            }
        }
    }
}

data class WaterBill(
    val id: String,
    val city: String,
    val provider: String,
    val consumerNumber: String,
    val outstandingAmount: Double,
    val dueDate: String,
    val consumerName: String,
    val meterReading: Int,
    val connectionType: String
)

private fun getCityWaterProviders(city: String): List<String> {
    return when (city) {
        "Bangalore" -> listOf("BWSSB - Water Board")
        "Mumbai" -> listOf("MWMC", "BMC Water", "Regional Water Board")
        "Delhi" -> listOf("DJB - Delhi Jal Board", "TPDDL Water")
        "Chennai" -> listOf("CMWSSB - Water Board", "Chennai Water Authority")
        "Hyderabad" -> listOf("HMWS&SB - Water Board", "Hyderabad Water Supply")
        "Pune" -> listOf("PIMWSSB - Water Board", "PMC Water Supply")
        "Ahmedabad" -> listOf("AUDA Water Board", "Ahmedabad Water Supply")
        "Kolkata" -> listOf("KMC Water Board", "Calcutta Water Authority")
        "Jaipur" -> listOf("JDN Water Board", "Jaipur Water Supply")
        "Lucknow" -> listOf("Lucknow Jal Nigam", "Water Supply Board")
        else -> listOf("Municipal Water Board", "Water Supply Authority")
    }
}
