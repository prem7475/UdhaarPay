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
import com.udhaarpay.app.data.model.CreditCard


val mockCards = listOf(
    CreditCard(
        id = 1,
        userId = "user_001",
        cardNumber = "4111 1111 1111 1111",
        cardHolderName = "Visa Platinum",
        expiryMonth = 12,
        expiryYear = 2028,
        cvv = "123",
        issuerBank = "HDFC",
        cardType = "VISA",
        cardColor = "#1E3A8A",
        limit = 120000.0,
        balanceUsed = 20000.0,
        isDefault = true,
        isActive = true,
        createdAt = System.currentTimeMillis()
    ),
    CreditCard(
        id = 2,
        userId = "user_001",
        cardNumber = "5555 5555 5555 4444",
        cardHolderName = "Mastercard Gold",
        expiryMonth = 9,
        expiryYear = 2026,
        cvv = "456",
        issuerBank = "SBI",
        cardType = "MASTERCARD",
        cardColor = "#F59E42",
        limit = 80000.0,
        balanceUsed = 10000.0,
        isDefault = false,
        isActive = false,
        createdAt = System.currentTimeMillis()
    )
)

@Composable
fun CreditCardScreen() {
    var showApply by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Your Credit Cards", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        LazyColumn {
            items(mockCards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                ) {
                    // ...existing code...
                }
            }
        }
    }
}