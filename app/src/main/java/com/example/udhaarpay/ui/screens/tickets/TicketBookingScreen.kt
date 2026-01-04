
package com.example.udhaarpay.ui.screens.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TicketBookingScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Flights", "Trains", "Buses")
    Column(Modifier.fillMaxSize().background(Color(0xFFF9FAFB)).padding(16.dp)) {
        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { i, title ->
                Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title) })
            }
        }
        Spacer(Modifier.height(16.dp))
        when (selectedTab) {
            0 -> FlightBookingTab()
            1 -> TrainBookingTab()
            2 -> BusBookingTab()
        }
    }
}

@Composable
fun FlightBookingTab() {
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var passengers by remember { mutableStateOf(1) }
    var showConfirm by remember { mutableStateOf(false) }
    val flights = listOf(
        "IndiGo 6E-123 | 10:00 - 12:00 | ₹4500",
        "Air India AI-456 | 13:00 - 15:30 | ₹5200",
        "SpiceJet SG-789 | 18:00 - 20:00 | ₹4800"
    )
    Column {
        OutlinedTextField(value = from, onValueChange = { from = it }, label = { Text("From Airport") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = to, onValueChange = { to = it }, label = { Text("To Airport") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Passengers: $passengers", modifier = Modifier.padding(end = 8.dp))
            Button(onClick = { if (passengers > 1) passengers-- }) { Text("-") }
            Button(onClick = { passengers++ }) { Text("+") }
        }
        Spacer(Modifier.height(8.dp))
        flights.forEach { flight ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { showConfirm = true }) {
                Box(Modifier.padding(12.dp)) { Text(flight) }
            }
        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Booking Confirmed") },
                text = { Text("Flight booked successfully!") },
                confirmButton = { TextButton(onClick = { showConfirm = false }) { Text("OK") } }
            )
        }
    }
}

@Composable
fun TrainBookingTab() {
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var showConfirm by remember { mutableStateOf(false) }
    val trains = listOf(
        "Rajdhani Express | 08:00 - 18:00 | ₹2200",
        "Shatabdi Express | 09:30 - 17:00 | ₹1800",
        "Local Express | 12:00 - 20:00 | ₹900"
    )
    Column {
        OutlinedTextField(value = from, onValueChange = { from = it }, label = { Text("From Station") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = to, onValueChange = { to = it }, label = { Text("To Station") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        trains.forEach { train ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { showConfirm = true }) {
                Box(Modifier.padding(12.dp)) { Text(train) }
            }
        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Booking Confirmed") },
                text = { Text("Train booked successfully!") },
                confirmButton = { TextButton(onClick = { showConfirm = false }) { Text("OK") } }
            )
        }
    }
}

@Composable
fun BusBookingTab() {
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var showConfirm by remember { mutableStateOf(false) }
    val buses = listOf(
        "RedBus | 21:00 - 06:00 | ₹1200",
        "Ixigo | 22:00 - 07:00 | ₹1100",
        "Goibibo | 23:00 - 08:00 | ₹1000"
    )
    Column {
        OutlinedTextField(value = from, onValueChange = { from = it }, label = { Text("From City") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = to, onValueChange = { to = it }, label = { Text("To City") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        buses.forEach { bus ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { showConfirm = true }) {
                Box(Modifier.padding(12.dp)) { Text(bus) }
            }
        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Booking Confirmed") },
                text = { Text("Bus booked successfully!") },
                confirmButton = { TextButton(onClick = { showConfirm = false }) { Text("OK") } }
            )
        }
    }
}
