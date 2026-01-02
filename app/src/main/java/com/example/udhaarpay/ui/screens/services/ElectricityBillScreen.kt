package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
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
fun ElectricityBillScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val accountNumber = remember { mutableStateOf("") }
    val selectedState = remember { mutableStateOf<String?>(null) }
    val selectedProvider = remember { mutableStateOf<String?>(null) }
    val billDetails = remember { mutableStateOf<ElectricityBill?>(null) }
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
        CommonComponents.LoadingDialog(message = "Fetching bill details...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top App Bar
        CommonComponents.PremiumTopAppBar(
            title = "Electricity Bill Payment",
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
                    leadingIcon = Icons.Default.Description,
                    keyboardType = KeyboardType.Number,
                    enabled = selectedProvider.value == null
                    ) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = "Account", tint = TextTertiary)
                    }
            }

            // State Selection
            if (accountNumber.value.length >= 8) {
                item {
                    Text(
                        text = "Select State",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StateSelectionDropdown(
                        selectedState = selectedState.value,
                        onStateSelected = { selectedState.value = it }
                    )
                }
            }

            // Provider Selection
            if (selectedState.value != null) {
                item {
                    Text(
                        text = "Select Provider",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProviderSelectionGrid(
                        state = selectedState.value ?: "",
                        selectedProvider = selectedProvider.value,
                        onProviderSelected = { provider ->
                            selectedProvider.value = provider
                            // Simulate API call to fetch bill
                            billDetails.value = ElectricityBill(
                                id = "elec_${System.currentTimeMillis()}",
                                state = selectedState.value ?: "",
                                provider = provider,
                                accountNumber = accountNumber.value,
                                outstandingAmount = (500..5000).random().toDouble(),
                                dueDate = "15/12/2025",
                                consumerName = "Rajesh Kumar",
                                meterNumber = "MH" + (10000..99999).random(),
                                connectionStatus = "Active"
                            )
                        }
                    )
                }
            }

            // Bill Details Display
            if (billDetails.value != null) {
                item {
                    BillDetailsCard(bill = billDetails.value!!)
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
                        onClick = {
                            // Navigate to payment or process bill payment
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
private fun StateSelectionDropdown(
    selectedState: String?,
    onStateSelected: (String) -> Unit
) {
    val states = listOf(
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
        "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh",
        "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra",
        "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
        "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
        "Uttar Pradesh", "Uttarakhand", "West Bengal"
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
                text = selectedState ?: "Select State",
                fontSize = 14.sp,
                color = if (selectedState != null) TextPrimary else TextSecondary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .background(DarkCard)
        ) {
            states.forEach { state ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = state,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    },
                    onClick = {
                        onStateSelected(state)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        if (selectedState == state) NeonOrange.copy(alpha = 0.2f) else DarkCard
                    )
                )
            }
        }
    }
}

@Composable
private fun ProviderSelectionGrid(
    state: String,
    selectedProvider: String?,
    onProviderSelected: (String) -> Unit
) {
    val providers = getStateProviders(state)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        providers.forEach { provider ->
            ProviderCard(
                name = provider,
                isSelected = selectedProvider == provider,
                onClick = { onProviderSelected(provider) }
            )
        }
    }
}

@Composable
private fun ProviderCard(
    name: String,
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = name,
                tint = if (isSelected) NeonOrange else AccentBlue,
                modifier = Modifier.size(28.dp)
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonOrange
                )
            }
        }
    }
}

@Composable
private fun BillDetailsCard(bill: ElectricityBill) {
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
                modifier = Modifier
                    .fillMaxWidth(),
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
                        text = "Status",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = bill.connectionStatus,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = SuccessGreen
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
                        text = "Meter Number",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = bill.meterNumber,
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
                    color = NeonOrange
                )
            }
        }
    }
}

data class ElectricityBill(
    val id: String,
    val state: String,
    val provider: String,
    val accountNumber: String,
    val outstandingAmount: Double,
    val dueDate: String,
    val consumerName: String,
    val meterNumber: String,
    val connectionStatus: String
)

private fun getStateProviders(state: String): List<String> {
    return when (state) {
        "Maharashtra" -> listOf("MSEDCL", "Reliance Power", "Torrent Power", "DDUGJNL")
        "Karnataka" -> listOf("BESCOM", "HESCOM", "KPTCL", "MESCOM")
        "Gujarat" -> listOf("GUVNL", "Torrent Power", "ONGC Power", "Reliance Power")
        "Tamil Nadu" -> listOf("TNEB", "TANGEDCO", "TNPL", "Solar Energy Corp")
        "Uttar Pradesh" -> listOf("UPPCL", "NPCL", "Deen Dayal Power", "Bundelkhand")
        "Delhi" -> listOf("BSES Yamuna", "BSES Rajdhani", "TPDDL", "NTPC")
        "Rajasthan" -> listOf("JVVNL", "PPARL", "AVVNL", "UGVCL")
        "Punjab" -> listOf("PSPCL", "HSIIDC", "Dakshin Haryana", "Northern Region")
        "West Bengal" -> listOf("WBSEDCL", "WBSETCL", "IESCO", "DESCO")
        else -> listOf("State Electricity Board", "NTPC", "Power Generation Company", "Private Utility")
    }
}
