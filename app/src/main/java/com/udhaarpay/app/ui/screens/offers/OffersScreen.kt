package com.udhaarpay.app.ui.screens.offers

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class Offer(
    val title: String,
    val description: String,
    val category: String,
    val validity: String
)

@Composable
fun OffersScreen() {
    val offers = listOf(
        Offer("10% Cashback on UPI", "Get 10% cashback on UPI above INR 500", "Shopping", "Valid till 28 Feb 2026"),
        Offer("Dining Gold Offer", "Flat INR 200 off at premium restaurants", "Dining", "Valid till 10 Mar 2026"),
        Offer("Travel Bonus Miles", "Earn 5x points on flights and hotels", "Travel", "Valid till 30 Mar 2026"),
        Offer("Movie Ticket Deal", "Flat INR 100 off on movie bookings", "Entertainment", "Valid till 12 Mar 2026"),
        Offer("Luxury Retail Coupon", "15% off at partner luxury stores", "Shopping", "Valid till 31 Mar 2026")
    )
    val categories = listOf("All", "Travel", "Dining", "Shopping", "Entertainment")

    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var cashbackEarned by remember { mutableStateOf(2450.0) }

    val filtered = offers.filter {
        val matchSearch = it.title.contains(search, ignoreCase = true) || it.description.contains(search, ignoreCase = true)
        val matchCategory = selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
        matchSearch && matchCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Cashback & Offers", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                Text("Cashback Earned", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("INR ${"%.2f".format(cashbackEarned)}", style = MaterialTheme.typography.headlineSmall)
                Text("Reward points and coupons are mock values stored locally.")
            }
        }
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Search coupons") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            categories.forEach { category ->
                TextButton(
                    onClick = { selectedCategory = category }
                ) {
                    Text(
                        category,
                        color = if (selectedCategory == category) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = filtered, key = { "${it.title}-${it.validity}" }) { offer ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(offer.title, fontWeight = FontWeight.SemiBold)
                        Text(offer.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Category: ${offer.category}")
                        Text(offer.validity, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}
