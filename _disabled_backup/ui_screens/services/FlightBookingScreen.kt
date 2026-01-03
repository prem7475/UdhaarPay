package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
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
fun FlightBookingScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val fromCity = remember { mutableStateOf("") }
    val toCity = remember { mutableStateOf("") }
    val departureDate = remember { mutableStateOf("") }
    val returnDate = remember { mutableStateOf("") }
    val passengers = remember { mutableStateOf(1) }
    val tripType = remember { mutableStateOf("OneWay") }
    val selectedFlight = remember { mutableStateOf<Flight?>(null) }
    val selectedSeat = remember { mutableStateOf<String?>(null) }
    val totalAmount = remember { mutableStateOf(0.0) }
    val remarks = remember { mutableStateOf("") }

    val errorMessage = error
    var showErrorDialog by remember { mutableStateOf(errorMessage != null) }

    LaunchedEffect(errorMessage) {
        showErrorDialog = errorMessage != null
    }

    LaunchedEffect(selectedFlight.value, passengers.value) {
        if (selectedFlight.value != null) {
            totalAmount.value = selectedFlight.value!!.price * passengers.value
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
        CommonComponents.LoadingDialog(message = "Searching flights...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonComponents.PremiumTopAppBar(
            title = "Book Flight",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Trip Type
            item {
                Text(
                    text = "Trip Type",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                TripTypeSelector(
                    tripType = tripType.value,
                    onTripTypeSelected = { tripType.value = it }
                )
            }

            // From City
            item {
                Text(
                    text = "From Airport",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                AirportInputField(
                    value = fromCity.value,
                    onValueChange = { fromCity.value = it },
                    placeholder = "Departure city"
                )
            }

            // To City
            item {
                Text(
                    text = "To Airport",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                AirportInputField(
                    value = toCity.value,
                    onValueChange = { toCity.value = it },
                    placeholder = "Arrival city"
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

            // Return Date (if round trip)
            if (tripType.value == "RoundTrip") {
                item {
                    Text(
                        text = "Return Date",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CommonComponents.PremiumTextField(
                        value = returnDate.value,
                        onValueChange = { returnDate.value = it },
                        label = "DD/MM/YYYY",
                        leadingIcon = Icons.Default.DateRange,
                        keyboardType = KeyboardType.Number
                    )
                }
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
                FlightPassengerSelector(
                    count = passengers.value,
                    onCountChanged = { passengers.value = it }
                )
            }

            // Flight Selection
            if (fromCity.value.isNotEmpty() && toCity.value.isNotEmpty() && departureDate.value.isNotEmpty()) {
                item {
                    Text(
                        text = "Available Flights",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlightSelectionList(
                        from = fromCity.value,
                        to = toCity.value,
                        selectedFlight = selectedFlight.value,
                        onFlightSelected = { selectedFlight.value = it }
                    )
                }
            }

            // Seat Selection
            if (selectedFlight.value != null) {
                item {
                    Text(
                        text = "Select Seat Class",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SeatClassSelector(
                        selectedSeat = selectedSeat.value,
                        onSeatSelected = { selectedSeat.value = it }
                    )
                }
            }

            // Amount Display
            if (selectedFlight.value != null && selectedSeat.value != null) {
                item {
                    FlightBookingAmountCard(
                        flight = selectedFlight.value!!,
                        passengers = passengers.value,
                        seatClass = selectedSeat.value!!,
                        totalAmount = totalAmount.value
                    )
                }
            }

            // Remarks
            if (selectedSeat.value != null) {
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
            if (selectedSeat.value != null) {
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
private fun TripTypeSelector(
    tripType: String,
    onTripTypeSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("OneWay", "RoundTrip").forEach { type ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (tripType == type) AccentYellow else DarkCard)
                    .clickable { onTripTypeSelected(type) }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (type == "OneWay") "One Way" else "Round Trip",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (tripType == type) DarkBackground else TextPrimary
                )
            }
        }
    }
}

@Composable
private fun AirportInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    CommonComponents.PremiumTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = placeholder,
        leadingIcon = Icons.Default.FlightTakeoff,
        keyboardType = KeyboardType.Text
    )
}

@Composable
private fun FlightPassengerSelector(
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
                .background(if (count > 1) AccentYellow else DarkCard)
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
                .background(if (count < 9) AccentYellow else DarkCard)
        ) {
            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
private fun FlightSelectionList(
    from: String,
    to: String,
    selectedFlight: Flight?,
    onFlightSelected: (Flight) -> Unit
) {
    val flights = getFlightsForRoute(from, to)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        flights.forEach { flight ->
            FlightOptionCard(
                flight = flight,
                isSelected = selectedFlight?.id == flight.id,
                onClick = { onFlightSelected(flight) }
            )
        }
    }
}

@Composable
private fun FlightOptionCard(
    flight: Flight,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) AccentYellow.copy(alpha = 0.2f) else DarkCard
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
                    text = flight.airline,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "₹${flight.price}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentYellow
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = flight.departureTime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = flight.from,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Icon(
                    imageVector = Icons.Default.FlightTakeoff,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = flight.arrivalTime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = flight.to,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Duration: ${flight.duration} | Stops: ${flight.stops} | Seats: ${flight.availableSeats}",
                fontSize = 11.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun SeatClassSelector(
    selectedSeat: String?,
    onSeatSelected: (String) -> Unit
) {
    val classes = listOf("Economy", "Premium Economy", "Business")

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        classes.forEach { cls ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selectedSeat == cls) AccentYellow.copy(alpha = 0.2f) else DarkCard
                    )
                    .clickable { onSeatSelected(cls) }
                    .padding(12.dp)
            ) {
                Text(
                    text = cls,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun FlightBookingAmountCard(
    flight: Flight,
    passengers: Int,
    seatClass: String,
    totalAmount: Double
) {
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
                text = "Flight Booking Summary",
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
                        text = "Airline",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = flight.airline,
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
                        text = seatClass,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Route: ${flight.from} → ${flight.to}",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Passengers: $passengers | Per ticket: ₹${flight.price}",
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
                    color = AccentYellow
                )
            }
        }
    }
}

data class Flight(
    val id: String = "",
    val airline: String = "",
    val from: String = "",
    val to: String = "",
    val departureTime: String = "",
    val arrivalTime: String = "",
    val duration: String = "",
    val stops: Int = 0,
    val price: Double = 0.0,
    val availableSeats: Int = 0
)

private fun getFlightsForRoute(from: String, to: String): List<Flight> {
    return listOf(
        Flight("f1", "Air India", from, to, "08:00 AM", "12:30 PM", "4h 30m", 0, 6500.0, 45),
        Flight("f2", "IndiGo", from, to, "10:15 AM", "03:00 PM", "4h 45m", 1, 5200.0, 62),
        Flight("f3", "SpiceJet", from, to, "02:30 PM", "07:15 PM", "4h 45m", 0, 4800.0, 38),
        Flight("f4", "Vistara", from, to, "04:45 PM", "09:30 PM", "4h 45m", 0, 7200.0, 28)
    )
}
