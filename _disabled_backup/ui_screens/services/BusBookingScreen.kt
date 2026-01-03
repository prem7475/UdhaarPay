package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
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
fun BusBookingScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val fromCity = remember { mutableStateOf("") }
    val toCity = remember { mutableStateOf("") }
    val departureDate = remember { mutableStateOf("") }
    val passengers = remember { mutableStateOf(1) }
    val selectedBus = remember { mutableStateOf<BusOption?>(null) }
    val selectedSeat = remember { mutableStateOf(0) }
    val totalAmount = remember { mutableStateOf(0.0) }
    val remarks = remember { mutableStateOf("") }

    val errorMessage = error
    var showErrorDialog by remember { mutableStateOf(errorMessage != null) }

    LaunchedEffect(errorMessage) {
        showErrorDialog = errorMessage != null
    }

    LaunchedEffect(selectedBus.value, passengers.value) {
        if (selectedBus.value != null) {
            totalAmount.value = selectedBus.value!!.price * passengers.value
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
        CommonComponents.LoadingDialog(message = "Searching buses...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonComponents.PremiumTopAppBar(
            title = "Book Bus Ticket",
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
                CommonComponents.PremiumTextField(
                    value = fromCity.value,
                    onValueChange = { fromCity.value = it },
                    label = "Departure city",
                    leadingIcon = Icons.Default.DirectionsBus,
                    keyboardType = KeyboardType.Text
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
                CommonComponents.PremiumTextField(
                    value = toCity.value,
                    onValueChange = { toCity.value = it },
                    label = "Arrival city",
                    leadingIcon = Icons.Default.DirectionsBus,
                    keyboardType = KeyboardType.Text
                )
            }

            // Departure Date
            item {
                Text(
                    text = "Departure Date",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CommonComponents.PremiumTextField(
                    value = departureDate.value,
                    onValueChange = { departureDate.value = it },
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
                BusPassengerSelector(
                    count = passengers.value,
                    onCountChanged = { passengers.value = it }
                )
            }

            // Bus Selection
            if (fromCity.value.isNotEmpty() && toCity.value.isNotEmpty() && departureDate.value.isNotEmpty()) {
                item {
                    Text(
                        text = "Available Buses",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BusSelectionList(
                        from = fromCity.value,
                        to = toCity.value,
                        selectedBus = selectedBus.value,
                        onBusSelected = { selectedBus.value = it }
                    )
                }
            }

            // Seat Selection
            if (selectedBus.value != null) {
                item {
                    Text(
                        text = "Select Seat",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SeatAvailabilityGrid(
                        selectedSeat = selectedSeat.value,
                        onSeatSelected = { selectedSeat.value = it }
                    )
                }
            }

            // Amount Display
            if (selectedBus.value != null) {
                item {
                    BusBookingAmountCard(
                        bus = selectedBus.value!!,
                        passengers = passengers.value,
                        totalAmount = totalAmount.value
                    )
                }
            }

            // Remarks
            if (selectedBus.value != null) {
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
            if (selectedBus.value != null) {
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
private fun BusPassengerSelector(
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
                .background(if (count > 1) AccentLime else DarkCard)
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
            onClick = { if (count < 8) onCountChanged(count + 1) },
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (count < 8) AccentLime else DarkCard)
        ) {
            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
private fun BusSelectionList(
    from: String,
    to: String,
    selectedBus: BusOption?,
    onBusSelected: (BusOption) -> Unit
) {
    val buses = getBusesForRoute(from, to)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buses.forEach { bus ->
            BusOptionCard(
                bus = bus,
                isSelected = selectedBus?.id == bus.id,
                onClick = { onBusSelected(bus) }
            )
        }
    }
}

@Composable
private fun BusOptionCard(
    bus: BusOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) AccentLime.copy(alpha = 0.2f) else DarkCard
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
                    text = bus.operator,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "₹${bus.price}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentLime
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = bus.departureTime,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Depart",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${bus.duration}h",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${bus.availableSeats} seats",
                        fontSize = 10.sp,
                        color = TextTertiary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = bus.arrivalTime,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Arrive",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Type: ${bus.type} | Amenities: ${bus.amenities}",
                fontSize = 10.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun SeatAvailabilityGrid(
    selectedSeat: Int,
    onSeatSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(5) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(5) { col ->
                    val seatNumber = row * 5 + col + 1
                    val isBooked = seatNumber in listOf(3, 7, 12, 18, 22)
                    val isSeat = seatNumber <= 40

                    if (isSeat) {
                        Box(
                            modifier = Modifier
                                .size(35.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when {
                                        isBooked -> DarkCard
                                        selectedSeat == seatNumber -> AccentLime
                                        else -> DarkCard
                                    }
                                )
                                .clickable(enabled = !isBooked) { onSeatSelected(seatNumber) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = seatNumber.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBooked || selectedSeat == seatNumber) TextPrimary else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(DarkCard)
                )
                Text("Available", fontSize = 10.sp, color = TextSecondary)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(ErrorRed)
                )
                Text("Booked", fontSize = 10.sp, color = TextSecondary)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AccentLime)
                )
                Text("Selected", fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun BusBookingAmountCard(
    bus: BusOption,
    passengers: Int,
    totalAmount: Double
) {
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
                text = "Booking Summary",
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
                        text = "Bus",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = bus.operator,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Type",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = bus.type,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Departure: ${bus.departureTime} | Arrival: ${bus.arrivalTime}",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Passengers: $passengers | Per ticket: ₹${bus.price}",
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
                    color = AccentLime
                )
            }
        }
    }
}

data class BusOption(
    val id: String,
    val operator: String,
    val type: String,        // AC, Non-AC, Sleeper
    val departureTime: String,
    val arrivalTime: String,
    val duration: Int,       // hours
    val price: Double,
    val availableSeats: Int,
    val amenities: String
)

private fun getBusesForRoute(from: String, to: String): List<BusOption> {
    return listOf(
        BusOption("b1", "RedBus Express", "AC", "08:00 AM", "04:30 PM", 8, 550.0, 32, "WiFi, Charging"),
        BusOption("b2", "GoIbibo Comfort", "AC Sleeper", "10:00 PM", "06:00 AM", 8, 750.0, 45, "Blanket, Pillow"),
        BusOption("b3", "MakeMyTrip Deluxe", "AC", "01:00 PM", "09:00 PM", 8, 650.0, 28, "WiFi, USB"),
        BusOption("b4", "TravelJourney Plus", "Non-AC", "06:00 AM", "02:00 PM", 8, 400.0, 55, "Fan, Water")
    )
}
