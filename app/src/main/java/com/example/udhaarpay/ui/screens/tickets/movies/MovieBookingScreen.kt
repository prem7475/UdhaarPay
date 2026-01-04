package com.example.udhaarpay.ui.screens.tickets.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Mock movie data
val movies = listOf(
    Movie("Pathaan", "Hindi", "2h 30m", "4.5", "Shah Rukh Khan, Deepika Padukone"),
    Movie("Oppenheimer", "English", "3h", "4.8", "Cillian Murphy, Emily Blunt"),
    Movie("Gadar 2", "Hindi", "2h 40m", "4.2", "Sunny Deol, Ameesha Patel"),
    Movie("Jawan", "Hindi", "2h 45m", "4.6", "Shah Rukh Khan, Nayanthara"),
    Movie("Animal", "Hindi", "2h 50m", "4.3", "Ranbir Kapoor, Rashmika Mandanna")
)

data class Movie(val name: String, val language: String, val duration: String, val rating: String, val cast: String)

data class Cinema(val name: String, val shows: List<String>)
val cinemas = listOf(
    Cinema("PVR Cinemas", listOf("10:00 AM", "1:00 PM", "4:00 PM", "7:00 PM")),
    Cinema("INOX", listOf("11:00 AM", "2:00 PM", "5:00 PM", "8:00 PM")),
    Cinema("Cinepolis", listOf("9:30 AM", "12:30 PM", "3:30 PM", "6:30 PM"))
)

@Composable
fun MovieBookingScreen() {
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }
    var selectedCinema by remember { mutableStateOf<Cinema?>(null) }
    var selectedShow by remember { mutableStateOf<String?>(null) }
    var selectedSeats by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }
    var showConfirmation by remember { mutableStateOf(false) }
    val seatPrice = 250
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        when {
            selectedMovie == null -> {
                Text("Select Movie", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                LazyColumn {
                    items(movies) { movie ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { selectedMovie = movie },
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(movie.name, fontWeight = FontWeight.Bold)
                                Text("${movie.language} | ${movie.duration} | ⭐ ${movie.rating}")
                                Text("Cast: ${movie.cast}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
            selectedCinema == null -> {
                Text("Select Cinema", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                LazyColumn {
                    items(cinemas) { cinema ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { selectedCinema = cinema },
                        ) {
                            Box(Modifier.padding(12.dp)) { Text(cinema.name) }
                        }
                    }
                }
            }
            selectedShow == null -> {
                Text("Select Show Time", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                selectedCinema?.shows?.forEach { show ->
                    Button(
                        onClick = { selectedShow = show },
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) { Text(show) }
                }
            }
            selectedSeats.size < 1 -> {
                Text("Select Seats", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                // 8 rows x 12 seats
                Column {
                    for (row in 1..8) {
                        Row {
                            for (col in 1..12) {
                                val seat = row to col
                                val isSelected = seat in selectedSeats
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(2.dp)
                                        .background(if (isSelected) Color(0xFF2563EB) else Color.LightGray, shape = MaterialTheme.shapes.small)
                                        .clickable { selectedSeats = if (isSelected) selectedSeats - seat else selectedSeats + seat },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { if (selectedSeats.isNotEmpty()) showConfirmation = true },
                    enabled = selectedSeats.isNotEmpty()
                ) { Text("Book Now") }
            }
            showConfirmation -> {
                val total = selectedSeats.size * seatPrice
                AlertDialog(
                    onDismissRequest = { showConfirmation = false },
                    title = { Text("Booking Confirmed") },
                    text = {
                        Text("Movie: ${selectedMovie?.name}\nCinema: ${selectedCinema?.name}\nShow: $selectedShow\nSeats: ${selectedSeats.size}\nTotal: ₹$total")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            // Reset all
                            selectedMovie = null
                            selectedCinema = null
                            selectedShow = null
                            selectedSeats = setOf()
                            showConfirmation = false
                        }) { Text("OK") }
                    }
                )
            }
        }
    }
}
