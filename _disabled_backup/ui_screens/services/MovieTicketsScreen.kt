package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.ui.components.CommonComponents
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.ServiceViewModel

@Composable
fun MovieTicketsScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val selectedCity = remember { mutableStateOf<String?>(null) }
    val selectedMovie = remember { mutableStateOf<Movie?>(null) }
    val selectedTheatre = remember { mutableStateOf<Theatre?>(null) }
    val selectedTickets = remember { mutableStateOf(1) }
    val totalAmount = remember { mutableStateOf(0.0) }
    val remarks = remember { mutableStateOf("") }

    val errorMessage = error
    var showErrorDialog by remember { mutableStateOf(errorMessage != null) }

    LaunchedEffect(errorMessage) {
        showErrorDialog = errorMessage != null
    }

    LaunchedEffect(selectedTheatre.value, selectedTickets.value) {
        if (selectedTheatre.value != null) {
            totalAmount.value = selectedTheatre.value!!.pricePerTicket * selectedTickets.value
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
        CommonComponents.LoadingDialog(message = "Fetching movie details...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonComponents.PremiumTopAppBar(
            title = "Book Movie Tickets",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // City Selection
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

            // Movie Selection
            if (selectedCity.value != null) {
                item {
                    Text(
                        text = "Select Movie",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MovieSelectionList(
                        city = selectedCity.value ?: "",
                        selectedMovie = selectedMovie.value,
                        onMovieSelected = {
                            selectedMovie.value = it
                            selectedTheatre.value = null
                        }
                    )
                }
            }

            // Theatre & Showtime Selection
            if (selectedMovie.value != null) {
                item {
                    Text(
                        text = "Select Theatre & Showtime",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TheatreSelectionList(
                        movie = selectedMovie.value ?: Movie(),
                        city = selectedCity.value ?: "",
                        selectedTheatre = selectedTheatre.value,
                        onTheatreSelected = { selectedTheatre.value = it }
                    )
                }
            }

            // Ticket Quantity Selection
            if (selectedTheatre.value != null) {
                item {
                    Text(
                        text = "Number of Tickets",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TicketQuantitySelector(
                        quantity = selectedTickets.value,
                        onQuantityChanged = { selectedTickets.value = it }
                    )
                }
            }

            // Amount Card
            if (selectedTheatre.value != null) {
                item {
                    BookingAmountCard(
                        movie = selectedMovie.value!!,
                        theatre = selectedTheatre.value!!,
                        tickets = selectedTickets.value,
                        totalAmount = totalAmount.value
                    )
                }
            }

            // Remarks
            if (selectedTheatre.value != null) {
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
            if (selectedTheatre.value != null) {
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
    val cities = listOf("Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Pune")
    var expanded by remember { mutableStateOf(false) }

    Box {
        CommonComponents.PremiumTextField(
            value = selectedCity ?: "",
            onValueChange = {},
            label = "Select city",
            leadingIcon = Icons.Default.LocationOn,
            enabled = false,
            modifier = Modifier.clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(DarkCard)
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city, color = TextPrimary) },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun MovieSelectionList(
    city: String,
    selectedMovie: Movie?,
    onMovieSelected: (Movie) -> Unit
) {
    val movies = getMoviesForCity(city)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        movies.forEach { movie ->
            MovieCard(
                movie = movie,
                isSelected = selectedMovie?.id == movie.id,
                onClick = { onMovieSelected(movie) }
            )
        }
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) AccentMagenta.copy(alpha = 0.2f) else DarkCard
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
                    text = movie.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.language + " • " + movie.genre,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rating: ${movie.rating} ⭐",
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }

            Icon(
                imageVector = Icons.Default.LocalMovies,
                contentDescription = movie.title,
                tint = if (isSelected) AccentMagenta else TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun TheatreSelectionList(
    movie: Movie,
    city: String,
    selectedTheatre: Theatre?,
    onTheatreSelected: (Theatre) -> Unit
) {
    val theatres = getTheatresForMovie(city, movie)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        theatres.forEach { theatre ->
            TheatreCard(
                theatre = theatre,
                isSelected = selectedTheatre?.id == theatre.id,
                onClick = { onTheatreSelected(theatre) }
            )
        }
    }
}

@Composable
private fun TheatreCard(
    theatre: Theatre,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) AccentMagenta.copy(alpha = 0.2f) else DarkCard
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
                    text = theatre.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "₹${theatre.pricePerTicket}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentMagenta
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = theatre.location,
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Showtimes: ${theatre.showtimes.joinToString(", ")}",
                fontSize = 11.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun TicketQuantitySelector(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit
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
            onClick = { if (quantity > 1) onQuantityChanged(quantity - 1) },
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (quantity > 1) AccentMagenta else DarkCard)
        ) {
            Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Text(
            text = "$quantity",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        IconButton(
            onClick = { if (quantity < 10) onQuantityChanged(quantity + 1) },
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (quantity < 10) AccentMagenta else DarkCard)
        ) {
            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
private fun BookingAmountCard(
    movie: Movie,
    theatre: Theatre,
    tickets: Int,
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
                        text = "Movie",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = movie.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Tickets",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "$tickets",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Theatre: ${theatre.name}",
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
                    color = AccentMagenta
                )
            }
        }
    }
}

data class Movie(
    val id: String = "",
    val title: String = "",
    val genre: String = "",
    val language: String = "",
    val rating: Double = 0.0,
    val duration: Int = 0
)

data class Theatre(
    val id: String,
    val name: String,
    val location: String,
    val pricePerTicket: Double,
    val showtimes: List<String>
)

private fun getMoviesForCity(city: String): List<Movie> {
    return listOf(
        Movie("m1", "Bollywood Action", "Action", "Hindi", 7.8, 150),
        Movie("m2", "Tamil Thriller", "Thriller", "Tamil", 8.1, 140),
        Movie("m3", "English Drama", "Drama", "English", 7.5, 160),
        Movie("m4", "Animated Adventure", "Animation", "Hindi", 8.3, 120),
        Movie("m5", "Comedy Series", "Comedy", "Hindi", 7.2, 130)
    )
}

private fun getTheatresForMovie(city: String, movie: Movie): List<Theatre> {
    return when (city) {
        "Mumbai" -> listOf(
            Theatre("t1", "Cineplex Premium", "Bandra", 250.0, listOf("10:00 AM", "1:30 PM", "4:45 PM", "8:00 PM")),
            Theatre("t2", "Star Theatre", "Marine Lines", 200.0, listOf("11:00 AM", "2:30 PM", "6:00 PM", "9:15 PM")),
            Theatre("t3", "IMAX Central", "Dadar", 350.0, listOf("12:00 PM", "3:30 PM", "7:00 PM"))
        )
        "Delhi" -> listOf(
            Theatre("t4", "PVR Elite", "CP", 280.0, listOf("10:30 AM", "1:45 PM", "5:15 PM", "8:45 PM")),
            Theatre("t5", "Inox Premium", "Saket", 240.0, listOf("9:30 AM", "12:45 PM", "4:00 PM", "7:30 PM")),
            Theatre("t6", "Carnival Cinema", "Dwarka", 220.0, listOf("11:00 AM", "2:15 PM", "5:45 PM", "9:00 PM"))
        )
        "Bangalore" -> listOf(
            Theatre("t7", "Forum Cinema", "Koramangala", 260.0, listOf("10:00 AM", "1:30 PM", "5:00 PM", "8:30 PM")),
            Theatre("t8", "Orion IMAX", "Whitefield", 330.0, listOf("11:30 AM", "3:00 PM", "7:00 PM")),
            Theatre("t9", "PVR Premium", "MG Road", 270.0, listOf("9:45 AM", "1:00 PM", "4:30 PM", "8:00 PM"))
        )
        else -> listOf(
            Theatre("t10", "Local Cinema", "City Center", 200.0, listOf("12:00 PM", "3:30 PM", "7:00 PM")),
            Theatre("t11", "Premium Hall", "Downtown", 250.0, listOf("1:00 PM", "4:00 PM", "8:00 PM"))
        )
    }
}
