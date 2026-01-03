package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Train
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
fun TrainBookingScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val fromCity = remember { mutableStateOf("") }
    val toCity = remember { mutableStateOf("") }
    val journeyDate = remember { mutableStateOf("") }
    val passengers = remember { mutableStateOf(1) }
    val selectedTrain = remember { mutableStateOf<TrainOption?>(null) }
    val selectedClass = remember { mutableStateOf<TrainClass?>(null) }
    val totalAmount = remember { mutableStateOf(0.0) }
    val remarks = remember { mutableStateOf("") }

    val errorMessage = error
    var showErrorDialog by remember { mutableStateOf(errorMessage != null) }

    LaunchedEffect(errorMessage) {
        showErrorDialog = errorMessage != null
    }

    LaunchedEffect(selectedClass.value, passengers.value) {
        if (selectedClass.value != null) {
            totalAmount.value = selectedClass.value!!.fare * passengers.value
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
        CommonComponents.LoadingDialog(message = "Searching trains...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonComponents.PremiumTopAppBar(
            title = "Book Train Ticket",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // From City
            item {
                Text(
                    text = "From City",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CityInputField(
                    value = fromCity.value,
                    onValueChange = { fromCity.value = it },
                    placeholder = "Departure city"
                )
            }

            // To City
            item {
                Text(
                    text = "To City",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CityInputField(
                    value = toCity.value,
                    onValueChange = { toCity.value = it },
                    placeholder = "Arrival city"
                )
            }

            // Journey Date
            item {
                Text(
                    text = "Journey Date",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CommonComponents.PremiumTextField(
                    value = journeyDate.value,
                    onValueChange = { journeyDate.value = it },
                    label = "DD/MM/YYYY",
                    leadingIcon = Icons.Default.DateRange,
                    keyboardType = KeyboardType.Number
                )
            }

            // Passengers
            item {
                Text(
                    text = "Number of Passengers",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                PassengerSelector(
                    count = passengers.value,
                    onCountChanged = { passengers.value = it }
                )
            }

            // Train Selection
            if (fromCity.value.isNotEmpty() && toCity.value.isNotEmpty() && journeyDate.value.isNotEmpty()) {
                item {
                    Text(
                        text = "Available Trains",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TrainSelectionList(
                        from = fromCity.value,
                        to = toCity.value,
                        selectedTrain = selectedTrain.value,
                        onTrainSelected = { selectedTrain.value = it }
                    )
                }
            }

            // Class Selection
            if (selectedTrain.value != null) {
                item {
                    Text(
                        text = "Select Class",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TrainClassSelection(
                        selectedClass = selectedClass.value,
                        onClassSelected = { selectedClass.value = it }
                    )
                }
            }

            // Amount Display
            if (selectedClass.value != null) {
                item {
                    TrainBookingAmountCard(
                        train = selectedTrain.value!!,
                        trainClass = selectedClass.value!!,
                        passengers = passengers.value,
                        totalAmount = totalAmount.value
                    )
                }
            }

            // Remarks
            if (selectedClass.value != null) {
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
            if (selectedClass.value != null) {
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
private fun CityInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    CommonComponents.PremiumTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = placeholder,
        leadingIcon = Icons.Default.Train,
        keyboardType = KeyboardType.Text
    )
}

@Composable
private fun PassengerSelector(
    count: Int,
    onCountChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkCard)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (count > 1) onCountChanged(count - 1) },
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (count > 1) AccentCyan else DarkCard)
        ) {
            Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Text(
            text = "$count",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        IconButton(
            onClick = { if (count < 9) onCountChanged(count + 1) },
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (count < 9) AccentCyan else DarkCard)
        ) {
            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
private fun TrainSelectionList(
    from: String,
    to: String,
    selectedTrain: TrainOption?,
    onTrainSelected: (TrainOption) -> Unit
) {
    val trains = getTrainsForRoute(from, to)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        trains.forEach { train ->
            TrainOptionCard(
                train = train,
                isSelected = selectedTrain?.id == train.id,
                onClick = { onTrainSelected(train) }
            )
        }
    }
}

@Composable
private fun TrainOptionCard(
    train: TrainOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) AccentCyan.copy(alpha = 0.2f) else DarkCard
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
                    text = train.number,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = train.type,
                    fontSize = 12.sp,
                    color = AccentCyan
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = train.departureTime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = train.from,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Icon(
                    imageVector = Icons.Default.Train,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = train.arrivalTime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = train.to,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Duration: ${train.duration} hrs | Seats: ${train.availableSeats}",
                fontSize = 11.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun TrainClassSelection(
    selectedClass: TrainClass?,
    onClassSelected: (TrainClass) -> Unit
) {
    val classes = listOf(
        TrainClass("ac1", "AC 1st", 2500.0),
        TrainClass("ac2", "AC 2nd", 1500.0),
        TrainClass("ac3", "AC 3rd", 900.0),
        TrainClass("sl", "Sleeper", 500.0)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        classes.forEach { cls ->
            TrainClassCard(
                trainClass = cls,
                isSelected = selectedClass?.id == cls.id,
                onClick = { onClassSelected(cls) }
            )
        }
    }
}

@Composable
private fun TrainClassCard(
    trainClass: TrainClass,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) AccentCyan.copy(alpha = 0.2f) else DarkCard
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = trainClass.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "₹${trainClass.fare}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AccentCyan
            )
        }
    }
}

@Composable
private fun TrainBookingAmountCard(
    train: TrainOption,
    trainClass: TrainClass,
    passengers: Int,
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
                text = "Booking Details",
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
                        text = "Train",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = train.number,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Class",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = trainClass.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Route: ${train.from} → ${train.to}",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Passengers: $passengers | Per ticket: ₹${trainClass.fare}",
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
                    text = "Total Amount",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )

                Text(
                    text = "₹$totalAmount",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
            }
        }
    }
}

data class TrainOption(
    val id: String,
    val number: String,
    val type: String,
    val from: String,
    val to: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: Int,
    val availableSeats: Int
)

data class TrainClass(
    val id: String,
    val name: String,
    val fare: Double
)

private fun getTrainsForRoute(from: String, to: String): List<TrainOption> {
    return listOf(
        TrainOption("t1", "12345 Express", "Express", from, to, "08:00 AM", "04:30 PM", 8, 45),
        TrainOption("t2", "54321 Rajdhani", "Rajdhani", from, to, "10:00 AM", "02:00 PM", 4, 32),
        TrainOption("t3", "98765 Shatabdi", "Shatabdi", from, to, "12:00 PM", "05:00 PM", 5, 28),
        TrainOption("t4", "24680 Local", "Local", from, to, "03:00 PM", "10:30 PM", 7, 60)
    )
}
