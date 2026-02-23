package com.udhaarpay.app.ui.screens.tickets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.local.entities.Ticket
import com.udhaarpay.app.ui.screens.common.InAppBrowserDialog
import com.udhaarpay.app.ui.viewmodel.TicketViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class TicketPartner(val title: String, val type: String, val url: String)

@Composable
fun TicketScreen(viewModel: TicketViewModel = hiltViewModel()) {
    val tickets by viewModel.tickets.collectAsState()
    val sortedTickets = remember(tickets) { tickets.sortedByDescending { it.date } }

    val partners = listOf(
        TicketPartner("Movies", "movie", "https://in.bookmyshow.com/"),
        TicketPartner("Flights", "flight", "https://www.skyscanner.co.in/"),
        TicketPartner("Trains", "train", "https://www.irctc.co.in/"),
        TicketPartner("Buses", "bus", "https://www.redbus.in/")
    )

    var selectedPartner by remember { mutableStateOf<TicketPartner?>(null) }
    var bookingType by remember { mutableStateOf("movie") }
    var bookingId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("BookMyShow") }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Ticket Booking", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            partners.take(2).forEach { partner ->
                Button(
                    onClick = {
                        selectedPartner = partner
                        bookingType = partner.type
                        provider = partner.title
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(partner.title)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            partners.drop(2).forEach { partner ->
                Button(
                    onClick = {
                        selectedPartner = partner
                        bookingType = partner.type
                        provider = partner.title
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(partner.title)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text("After booking in webview, add booking manually", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = bookingType,
                    onValueChange = { bookingType = it },
                    label = { Text("Type (movie/flight/train/bus)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = provider,
                    onValueChange = { provider = it },
                    label = { Text("Provider") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = bookingId,
                    onValueChange = { bookingId = it },
                    label = { Text("Booking ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
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
                ) {
                    Text("Add Booking")
                }
                if (!message.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(message ?: "", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Text("Saved Tickets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = sortedTickets, key = { it.ticketId }) { ticket ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("${ticket.ticketType.uppercase(Locale.getDefault())} | ${ticket.provider}", fontWeight = FontWeight.SemiBold)
                        Text(ticket.destination ?: ticket.movieName ?: "-", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(ticket.date)),
                            fontSize = 12.sp
                        )
                        Text("INR ${"%.2f".format(ticket.amount)}", color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                    }
                }
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
