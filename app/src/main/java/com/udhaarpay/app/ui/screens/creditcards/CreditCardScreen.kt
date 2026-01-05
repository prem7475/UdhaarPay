package com.udhaarpay.app.ui.screens.creditcards


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
import com.udhaarpay.app.data.local.entities.CreditCard


val mockCards = listOf(
    CreditCard(
        id = 1,
        userId = "user_001",
        cardNumber = "4111 1111 1111 1111",
        cardHolderName = "Visa Platinum",
        expiryDate = "12/28",
        cvv = "123"
    ),
    CreditCard(
        id = 2,
        userId = "user_001",
        cardNumber = "5555 5555 5555 4444",
        cardHolderName = "Mastercard Gold",
        expiryDate = "09/26",
        cvv = "456"
    )
)

@Composable
fun CreditCardScreen() {
    var showApply by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(20.dp)
    ) {
        Text("Your Credit Cards", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
        Spacer(Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mockCards) { card ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(card.cardHolderName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                            Text(card.cardNumber, fontSize = 15.sp, color = Color(0xFFCBD5E1))
                        }
                    }
                }
            }
        }
    }
}