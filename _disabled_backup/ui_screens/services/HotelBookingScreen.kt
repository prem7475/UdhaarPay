package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.People
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
fun HotelBookingScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val city = remember { mutableStateOf("") }
    val checkInDate = remember { mutableStateOf("") }
    val checkOutDate = remember { mutableStateOf("") }
    val rooms = remember { mutableStateOf(1) }
    val guests = remember { mutableStateOf(2) }
    val selectedHotel = remember { mutableStateOf<Hotel?>(null) }
    val roomType = remember { mutableStateOf("Deluxe") }
    val nights = remember { mutableStateOf(0) }
    val totalAmount = remember { mutableStateOf(0.0) }
    val remarks = remember { mutableStateOf("") }

    val errorMessage = error
    var showErrorDialog by remember { mutableStateOf(errorMessage != null) }

    LaunchedEffect(errorMessage) {
        showErrorDialog = errorMessage != null
    }

    LaunchedEffect(selectedHotel.value, roomType.value, nights.value, rooms.value) {
        if (selectedHotel.value != null && nights.value > 0) {
            val pricePerRoom = when (roomType.value) {
                "Standard" -> selectedHotel.value!!.standardPrice
                "Deluxe" -> selectedHotel.value!!.deluxePrice
                else -> selectedHotel.value!!.suitePrice
            }
            totalAmount.value = pricePerRoom * nights.value * rooms.value
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
        CommonComponents.LoadingDialog(message = "Searching hotels...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonComponents.PremiumTopAppBar(
            title = "Book Hotel",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // City
            item {
                Text(
                    text = "City",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CommonComponents.PremiumTextField(
                    value = city.value,
                    onValueChange = { city.value = it },
                    label = "Enter city",
                    leadingIcon = Icons.Default.Hotel,
                    keyboardType = KeyboardType.Text
                )
            }

            // Check-in Date
            item {
                Text(
                    text = "Check-in Date",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CommonComponents.PremiumTextField(
                    value = checkInDate.value,
                    onValueChange = { checkInDate.value = it },
                    label = "DD/MM/YYYY",
                    leadingIcon = Icons.Default.DateRange,
                    keyboardType = KeyboardType.Number
                )
            }

            // Check-out Date
            item {
                Text(
                    text = "Check-out Date",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                CommonComponents.PremiumTextField(
                    value = checkOutDate.value,
                    onValueChange = { checkOutDate.value = it },
                    label = "DD/MM/YYYY",
                    leadingIcon = Icons.Default.DateRange,
                    keyboardType = KeyboardType.Number
                )
            }

            // Rooms & Guests
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Rooms",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HotelRoomSelector(
                            count = rooms.value,
                            onCountChanged = { rooms.value = it }
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Guests",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HotelGuestSelector(
                            count = guests.value,
                            onCountChanged = { guests.value = it }
                        )
                    }
                }
            }

            // Hotel Selection
            if (city.value.isNotEmpty() && checkInDate.value.isNotEmpty() && checkOutDate.value.isNotEmpty()) {
                item {
                    if (checkOutDate.value.isNotEmpty() && checkInDate.value.isNotEmpty()) {
                        val checkOutDay = checkOutDate.value.substringBefore("/").toIntOrNull() ?: 0
                        val checkInDay = checkInDate.value.substringBefore("/").toIntOrNull() ?: 0
                        nights.value = if (checkOutDay > checkInDay) checkOutDay - checkInDay else 1
                    }

                    Text(
                        text = "Available Hotels",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HotelSelectionList(
                        city = city.value,
                        selectedHotel = selectedHotel.value,
                        onHotelSelected = { selectedHotel.value = it }
                    )
                }
            }

            // Room Type
            if (selectedHotel.value != null) {
                item {
                    Text(
                        text = "Select Room Type",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HotelRoomTypeSelection(
                        selectedType = roomType.value,
                        onTypeSelected = { roomType.value = it }
                    )
                }
            }

            // Booking Summary
            if (selectedHotel.value != null && nights.value > 0) {
                item {
                    HotelBookingAmountCard(
                        hotel = selectedHotel.value!!,
                        roomType = roomType.value,
                        nights = nights.value,
                        rooms = rooms.value,
                        totalAmount = totalAmount.value
                    )
                }
            }

            // Remarks
            if (selectedHotel.value != null) {
                item {
                    Text(
                        text = "Special Requests (Optional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CommonComponents.PremiumTextField(
                        value = remarks.value,
                        onValueChange = { remarks.value = it },
                        label = "Add notes (non-smoking, early checkin, etc)",
                        singleLine = false
                    )
                }
            }

            // Proceed Button
            if (selectedHotel.value != null && nights.value > 0) {
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
private fun HotelRoomSelector(
    count: Int,
    onCountChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkCard)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (count > 1) onCountChanged(count - 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Text("-", fontSize = 16.sp, color = TextPrimary)
        }

        Text(
            text = "$count",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        IconButton(
            onClick = { if (count < 5) onCountChanged(count + 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Text("+", fontSize = 16.sp, color = TextPrimary)
        }
    }
}

@Composable
private fun HotelGuestSelector(
    count: Int,
    onCountChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkCard)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (count > 1) onCountChanged(count - 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Text("-", fontSize = 16.sp, color = TextPrimary)
        }

        Text(
            text = "$count",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        IconButton(
            onClick = { if (count < 10) onCountChanged(count + 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Text("+", fontSize = 16.sp, color = TextPrimary)
        }
    }
}

@Composable
private fun HotelSelectionList(
    city: String,
    selectedHotel: Hotel?,
    onHotelSelected: (Hotel) -> Unit
) {
    val hotels = getHotelsForCity(city)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        hotels.forEach { hotel ->
            HotelCard(
                hotel = hotel,
                isSelected = selectedHotel?.id == hotel.id,
                onClick = { onHotelSelected(hotel) }
            )
        }
    }
}

@Composable
private fun HotelCard(
    hotel: Hotel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) AccentTeal.copy(alpha = 0.2f) else DarkCard
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
                    text = hotel.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "⭐ ${hotel.rating}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentTeal
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = hotel.location,
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Standard: ₹${hotel.standardPrice}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Text(
                    text = "Deluxe: ₹${hotel.deluxePrice}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Text(
                    text = "Suite: ₹${hotel.suitePrice}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${hotel.rooms} rooms • ${hotel.amenities}",
                fontSize = 10.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun HotelRoomTypeSelection(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val types = listOf("Standard", "Deluxe", "Suite")

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        types.forEach { type ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selectedType == type) AccentTeal.copy(alpha = 0.2f) else DarkCard
                    )
                    .clickable { onTypeSelected(type) }
                    .padding(12.dp)
            ) {
                Text(
                    text = type,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedType == type) AccentTeal else TextPrimary
                )
            }
        }
    }
}

@Composable
private fun HotelBookingAmountCard(
    hotel: Hotel,
    roomType: String,
    nights: Int,
    rooms: Int,
    totalAmount: Double
) {
    val pricePerRoom = when (roomType) {
        "Standard" -> hotel.standardPrice
        "Deluxe" -> hotel.deluxePrice
        else -> hotel.suitePrice
    }

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
                        text = "Hotel",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = hotel.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Room Type",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = roomType,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "$nights nights • $rooms room(s)",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Price per room per night: ₹$pricePerRoom",
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
                    color = AccentTeal
                )
            }
        }
    }
}

data class Hotel(
    val id: String,
    val name: String,
    val location: String,
    val rating: Double,
    val standardPrice: Double,
    val deluxePrice: Double,
    val suitePrice: Double,
    val rooms: Int,
    val amenities: String
)

private fun getHotelsForCity(city: String): List<Hotel> {
    return listOf(
        Hotel("h1", "Grand Plaza", "City Center", 4.5, 2500.0, 3500.0, 5500.0, 150, "Pool, Gym, WiFi"),
        Hotel("h2", "Comfort Inn", "Downtown", 4.2, 1800.0, 2500.0, 4000.0, 120, "WiFi, Restaurant"),
        Hotel("h3", "Luxury Suites", "Business District", 4.8, 3500.0, 5000.0, 8000.0, 80, "Spa, Concierge"),
        Hotel("h4", "Budget Hotel", "Near Airport", 3.8, 1200.0, 1600.0, 2500.0, 200, "WiFi, 24hrs")
    )
}
