package com.udhaarpay.app.ui.screens.tickets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.local.entities.Ticket
import com.udhaarpay.app.ui.components.PremiumActionCard
import com.udhaarpay.app.ui.components.PremiumInfoCard
import com.udhaarpay.app.ui.components.PremiumPill
import com.udhaarpay.app.ui.components.PremiumScreen
import com.udhaarpay.app.ui.components.PremiumSectionHeader
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.screens.common.InAppBrowserDialog
import com.udhaarpay.app.ui.viewmodel.TicketViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class TicketPartner(val title: String, val type: String, val url: String, val hint: String)

@Composable
fun TicketScreen(viewModel: TicketViewModel = hiltViewModel()) {
    val tickets by viewModel.tickets.collectAsState()
    val sortedTickets = remember(tickets) { tickets.sortedByDescending { it.date } }

    val partners = listOf(
        TicketPartner("Movies", "movie", "https://in.bookmyshow.com/", "Seats, snacks, and quick booking"),
        TicketPartner("Flights", "flight", "https://www.skyscanner.co.in/", "Trip planning and fare discovery"),
        TicketPartner("Trains", "train", "https://www.irctc.co.in/", "Tickets and travel planning"),
        TicketPartner("Buses", "bus", "https://www.redbus.in/", "Routes and seat options")
    )

    var selectedPartner by remember { mutableStateOf<TicketPartner?>(null) }
    var bookingType by remember { mutableStateOf("movie") }
    var bookingId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("BookMyShow") }
    var message by remember { mutableStateOf<String?>(null) }

    PremiumScreen {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                PremiumSectionHeader(
                    title = "Bookings",
                    subtitle = "Movies, flights, trains, and buses in one calm premium flow"
                )
            }

            item {
                PremiumInfoCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Open provider portals", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Use the webview launch for inspiration, then save the booking locally for your mock app.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            partners.take(2).forEach { partner ->
                                PremiumActionCard(
                                    title = partner.title,
                                    subtitle = partner.hint,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        selectedPartner = partner
                                        bookingType = partner.type
                                        provider = partner.title
                                    }
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            partners.drop(2).forEach { partner ->
                                PremiumActionCard(
                                    title = partner.title,
                                    subtitle = partner.hint,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        selectedPartner = partner
                                        bookingType = partner.type
                                        provider = partner.title
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                PremiumInfoCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Saved booking form", fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            PremiumPill(text = "Movie", selected = bookingType == "movie", modifier = Modifier.weight(1f)) {
                                bookingType = "movie"
                            }
                            PremiumPill(text = "Flight", selected = bookingType == "flight", modifier = Modifier.weight(1f)) {
                                bookingType = "flight"
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            PremiumPill(text = "Train", selected = bookingType == "train", modifier = Modifier.weight(1f)) {
                                bookingType = "train"
                            }
                            PremiumPill(text = "Bus", selected = bookingType == "bus", modifier = Modifier.weight(1f)) {
                                bookingType = "bus"
                            }
                        }

                        OutlinedTextField(
                            value = provider,
                            onValueChange = { provider = it },
                            label = { Text("Provider") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = bookingId,
                            onValueChange = { bookingId = it },
                            label = { Text("Booking / reference ID") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                            label = { Text("Amount") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        UdhaarPayButton(
                            text = "Save Booking",
                            onClick = {
                                val amountValue = amount.toDoubleOrNull()
                                when {
                                    bookingId.isBlank() -> message = "Booking ID is required"
                                    amountValue == null || amountValue <= 0 -> message = "Enter valid amount"
                                    else -> {
                                        viewModel.insert(
                                            Ticket(
                                                ticketType = bookingType.lowercase(Locale.getDefault()),
                                                movieName = if (bookingType.equals("movie", true)) bookingId else null,
                                                destination = if (bookingType.equals("movie", true)) null else bookingId,
                                                cinema = if (bookingType.equals("movie", true)) provider else null,
                                                provider = provider,
                                                date = System.currentTimeMillis(),
                                                seats = null,
                                                amount = amountValue,
                                                status = "confirmed"
                                            )
                                        )
                                        message = "Booking saved locally."
                                        bookingId = ""
                                        amount = ""
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (!message.isNullOrBlank()) {
                            Text(message.orEmpty(), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            item {
                PremiumSectionHeader(
                    title = "Saved Tickets",
                    subtitle = "Your mock bookings are stored locally"
                )
            }

            items(sortedTickets, key = { it.ticketId }) { ticket ->
                PremiumInfoCard {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "${ticket.ticketType.uppercase(Locale.getDefault())} | ${ticket.provider}",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(ticket.destination ?: ticket.movieName ?: "-", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(ticket.date)),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "INR ${"%.2f".format(ticket.amount)}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    selectedPartner?.let { partner ->
        InAppBrowserDialog(
            title = partner.title,
            url = partner.url,
            onDismiss = { selectedPartner = null }
        )
    }
}
