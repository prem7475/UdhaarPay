package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalShipping
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
fun GasCylinderScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val phoneNumber = remember { mutableStateOf("") }
    val selectedCylinderType = remember { mutableStateOf<GasCylinder?>(null) }
    val quantity = remember { mutableStateOf(1) }
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
        CommonComponents.LoadingDialog(message = "Processing booking...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonComponents.PremiumTopAppBar(
            title = "Book Gas Cylinder",
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
                    text = "Phone Number",
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
                    leadingIcon = Icons.Default.LocalShipping,
                    keyboardType = KeyboardType.Number,
                    enabled = selectedCylinderType.value == null
                )
            }

            // Cylinder Type Selection
            if (phoneNumber.value.length == 10) {
                item {
                    Text(
                        text = "Select Cylinder Type",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CylinderTypeGrid(
                        selectedCylinder = selectedCylinderType.value,
                        onCylinderSelected = { selectedCylinderType.value = it }
                    )
                }
            }

            // Quantity Selection
            if (selectedCylinderType.value != null) {
                item {
                    Text(
                        text = "Quantity",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QuantitySelector(
                        quantity = quantity.value,
                        onQuantityChange = { quantity.value = it },
                        maxQuantity = 5
                    )
                }
            }

            // Booking Summary
            if (selectedCylinderType.value != null) {
                item {
                    BookingSummaryCard(
                        cylinder = selectedCylinderType.value!!,
                        quantity = quantity.value
                    )
                }
            }

            // Remarks
            if (selectedCylinderType.value != null) {
                item {
                    Text(
                        text = "Delivery Instructions (Optional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CommonComponents.PremiumTextField(
                        value = remarks.value,
                        onValueChange = { remarks.value = it },
                        label = "e.g., Leave at door, Ring doorbell",
                        singleLine = false
                    )
                }
            }

            // Proceed Button
            if (selectedCylinderType.value != null) {
                item {
                    CommonComponents.PremiumButton(
                        text = "Confirm Booking",
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
private fun CylinderTypeGrid(
    selectedCylinder: GasCylinder?,
    onCylinderSelected: (GasCylinder) -> Unit
) {
    val cylinders = listOf(
        GasCylinder(
            id = "cyl1",
            type = "Regular",
            capacity = 14.2,
            price = 899.0,
            deliveryTime = "24 hours",
            features = "Standard LPG cylinder"
        ),
        GasCylinder(
            id = "cyl2",
            type = "Premium",
            capacity = 19.0,
            price = 1199.0,
            deliveryTime = "24 hours",
            features = "Large capacity LPG"
        ),
        GasCylinder(
            id = "cyl3",
            type = "Autogas",
            capacity = 60.0,
            price = 3499.0,
            deliveryTime = "2-3 days",
            features = "For vehicle fuel"
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cylinders.forEach { cylinder ->
            CylinderCard(
                cylinder = cylinder,
                isSelected = selectedCylinder?.id == cylinder.id,
                onClick = { onCylinderSelected(cylinder) }
            )
        }
    }
}

@Composable
private fun CylinderCard(
    cylinder: GasCylinder,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) NeonOrange.copy(alpha = 0.15f) else DarkCard
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = cylinder.type,
                            tint = if (isSelected) NeonOrange else AccentRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = cylinder.type,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Selected",
                        tint = NeonOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = "${cylinder.capacity} kg",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = cylinder.features,
                fontSize = 11.sp,
                color = TextTertiary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Delivery",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = cylinder.deliveryTime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "₹${cylinder.price.toLong()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonOrange
                )
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    maxQuantity: Int = 5
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkCard)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
            modifier = Modifier.size(40.dp)
        ) {
            Text("-", fontSize = 20.sp, color = NeonOrange)
        }

        Text(
            text = "$quantity",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth()
        )

        IconButton(
            onClick = { if (quantity < maxQuantity) onQuantityChange(quantity + 1) },
            modifier = Modifier.size(40.dp)
        ) {
            Text("+", fontSize = 20.sp, color = NeonOrange)
        }
    }
}

@Composable
private fun BookingSummaryCard(
    cylinder: GasCylinder,
    quantity: Int
) {
    val totalPrice = cylinder.price * quantity

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(listOf(CardGradient3Start, CardGradient3End))
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Booking Summary",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cylinder Type",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = cylinder.type,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Quantity",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "$quantity cylinder(s)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = cylinder.deliveryTime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }

            Divider(color = TextSecondary.copy(alpha = 0.2f), thickness = 1.dp)

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
                    text = "₹${totalPrice.toLong()}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonOrange
                )
            }
        }
    }
}

data class GasCylinder(
    val id: String,
    val type: String,
    val capacity: Double,
    val price: Double,
    val deliveryTime: String,
    val features: String = ""
)
