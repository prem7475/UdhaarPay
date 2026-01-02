package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Tv
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
fun DTHRechargeScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val subscriberId = remember { mutableStateOf("") }
    val selectedProvider = remember { mutableStateOf<String?>(null) }
    val selectedPackage = remember { mutableStateOf<DTHPackage?>(null) }
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
        CommonComponents.LoadingDialog(message = "Processing DTH recharge...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonComponents.PremiumTopAppBar(
            title = "DTH Recharge",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Subscriber ID Input
            item {
                Text(
                    text = "Subscriber ID",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CommonComponents.PremiumTextField(
                    value = subscriberId.value,
                    onValueChange = {
                        if (it.length <= 15) subscriberId.value = it
                    },
                    label = "Enter subscriber ID",
                    leadingIcon = Icons.Default.SmartDisplay,
                    keyboardType = KeyboardType.Number,
                    enabled = selectedPackage.value == null
                )
            }

            // Provider Selection
            if (subscriberId.value.length >= 6) {
                item {
                    Text(
                        text = "Select Provider",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DTHProviderGrid(
                        selectedProvider = selectedProvider.value,
                        onProviderSelected = { selectedProvider.value = it }
                    )
                }
            }

            // Package Selection
            if (selectedProvider.value != null) {
                item {
                    Text(
                        text = "Select Package",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DTHPackageList(
                        provider = selectedProvider.value ?: "",
                        selectedPackage = selectedPackage.value,
                        onPackageSelected = {
                            selectedPackage.value = it
                            amount.value = it.price.toString()
                        }
                    )
                }
            }

            // Amount Display
            if (selectedPackage.value != null) {
                item {
                    DTHAmountCard(
                        dthPackage = selectedPackage.value!!,
                        amount = amount.value.toDoubleOrNull() ?: 0.0
                    )
                }
            }

            // Remarks
            if (selectedPackage.value != null) {
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
            if (selectedPackage.value != null) {
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
private fun DTHProviderGrid(
    selectedProvider: String?,
    onProviderSelected: (String) -> Unit
) {
    val providers = listOf("Dish TV", "Tata Sky", "Sun Direct", "Videocon")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        providers.forEach { provider ->
            DTHProviderCard(
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
private fun DTHProviderCard(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AccentPurple else DarkCard)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.Tv,
                contentDescription = name,
                tint = if (isSelected) DarkBackground else AccentPurple,
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
private fun DTHPackageList(
    provider: String,
    selectedPackage: DTHPackage?,
    onPackageSelected: (DTHPackage) -> Unit
) {
    val packages = getDTHPackages(provider)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        packages.forEach { pkg ->
            DTHPackageCard(
                dthPackage = pkg,
                isSelected = selectedPackage?.id == pkg.id,
                onClick = { onPackageSelected(pkg) }
            )
        }
    }
}

@Composable
private fun DTHPackageCard(
    dthPackage: DTHPackage,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) AccentPurple.copy(alpha = 0.2f) else DarkCard
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
                    text = dthPackage.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${dthPackage.channels} Channels • ${dthPackage.validity} days",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                if (dthPackage.highlights.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dthPackage.highlights,
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                }
            }

            Text(
                text = "₹${dthPackage.price}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AccentPurple
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Tv,
                contentDescription = "Selected",
                tint = AccentPurple,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
            )
        }
    }
}

@Composable
private fun DTHAmountCard(
    dthPackage: DTHPackage,
    amount: Double
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = CardGradient3Start
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Recharge Summary",
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
                        text = "Package",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = dthPackage.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Channels",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = dthPackage.channels.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Validity: ${dthPackage.validity} days",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Highlights: ${dthPackage.highlights}",
                fontSize = 11.sp,
                color = TextTertiary
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
                    text = "Total Amount",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )

                Text(
                    text = "₹$amount",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentPurple
                )
            }
        }
    }
}

data class DTHPackage(
    val id: String,
    val name: String,
    val channels: Int,
    val price: Double,
    val validity: Int,  // days
    val highlights: String = ""
)

private fun getDTHPackages(provider: String): List<DTHPackage> {
    return when (provider) {
        "Dish TV" -> listOf(
            DTHPackage("dtv1", "Hindi Entry", 50, 199.0, 28, "Hindi channels"),
            DTHPackage("dtv2", "English Plus", 80, 349.0, 28, "English + Sports"),
            DTHPackage("dtv3", "Premium HD", 150, 649.0, 30, "All channels in HD"),
            DTHPackage("dtv4", "Sports Max", 120, 499.0, 30, "5 Sports channels")
        )
        "Tata Sky" -> listOf(
            DTHPackage("ts1", "Basic", 50, 229.0, 28, "Regional + News"),
            DTHPackage("ts2", "Elite", 100, 399.0, 30, "Entertainment focus"),
            DTHPackage("ts3", "Premium", 180, 749.0, 30, "All HD + Movie channels"),
            DTHPackage("ts4", "Cricket Plus", 110, 549.0, 30, "Sports & Movies")
        )
        "Sun Direct" -> listOf(
            DTHPackage("sd1", "Tamil Gold", 60, 189.0, 28, "Tamil content"),
            DTHPackage("sd2", "Mixed Pack", 90, 349.0, 30, "Tamil + English"),
            DTHPackage("sd3", "Platinum", 140, 599.0, 30, "All languages HD"),
            DTHPackage("sd4", "Family Pack", 100, 429.0, 30, "Family entertainment")
        )
        "Videocon" -> listOf(
            DTHPackage("vc1", "Entry Level", 45, 179.0, 28, "Basic channels"),
            DTHPackage("vc2", "Standard", 85, 329.0, 30, "Standard + Sports"),
            DTHPackage("vc3", "Deluxe", 130, 579.0, 30, "Premium HD package"),
            DTHPackage("vc4", "Ultimate", 160, 699.0, 30, "Complete entertainment")
        )
        else -> emptyList()
    }
}
