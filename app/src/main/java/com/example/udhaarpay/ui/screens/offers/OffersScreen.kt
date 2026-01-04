package com.example.udhaarpay.ui.screens.offers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val mockOffers = listOf(
    Offer("10% Cashback on UPI", "Get 10% cashback on your next UPI payment above ₹500.", "Valid till 10 Jan 2026"),
    Offer("Movie Ticket Discount", "Flat ₹100 off on PVR bookings via UdhaarPay.", "Valid till 15 Jan 2026"),
    Offer("Bill Payment Reward", "Pay any utility bill and get ₹50 wallet credit.", "Valid till 20 Jan 2026"),
    Offer("Refer & Earn", "Refer a friend and earn ₹200 for each successful signup.", "No expiry")
)
data class Offer(val title: String, val description: String, val validity: String)

@Composable
fun OffersScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Offers & Rewards", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFFF59E0B))
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(mockOffers) { offer ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(offer.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFF59E0B))
                        Text(offer.description, fontSize = 13.sp, color = Color(0xFF64748B), modifier = Modifier.padding(top = 4.dp))
                        Text(offer.validity, fontSize = 12.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }
    }
}
