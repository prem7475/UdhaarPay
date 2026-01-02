package com.example.udhaarpay.ui.screens.wallet

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.AddCardResult
import com.example.udhaarpay.ui.viewmodel.CardViewModel

@Composable
fun AddCardScreen(
    onBack: () -> Unit,
    viewModel: CardViewModel = hiltViewModel()
) {
    val addCardState by viewModel.addCardState.collectAsState()
    val context = LocalContext.current

    var cardNumber by remember { mutableStateOf("") }
    var holderName by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    
    // Reset state on entry
    LaunchedEffect(Unit) {
        viewModel.resetAddCardState()
    }

    // Listen for Success/Error
    LaunchedEffect(addCardState) {
        when (addCardState) {
            is AddCardResult.Success -> {
                Toast.makeText(context, "Card Linked Successfully!", Toast.LENGTH_SHORT).show()
                onBack()
            }
            is AddCardResult.Error -> {
                // Error is shown as text below
            }
            null -> {}
        }
    }

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            SmallTopAppBar(
                title = { Text("Link Credit Card", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = PureBlack)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Card Preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkZinc),
                border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Text("RuPay", color = Color(0xFF00B0FF), fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopEnd))
                    
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text(
                            text = cardNumber.ifEmpty { "XXXX XXXX XXXX XXXX" },
                            color = White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp
                        )
                    }
                    
                    Row(
                        modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Card Holder", color = Zinc400, fontSize = 10.sp)
                            Text(holderName.ifEmpty { "YOUR NAME" }, color = White, fontWeight = FontWeight.Medium)
                        }
                        Column {
                            Text("Expiry", color = Zinc400, fontSize = 10.sp)
                            Text(expiry.ifEmpty { "MM/YY" }, color = White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Form Fields
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 16) cardNumber = it },
                label = { Text("Card Number") },
                leadingIcon = { Icon(Icons.Default.CreditCard, null, tint = Zinc400) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Zinc800
                )
            )

            OutlinedTextField(
                value = holderName,
                onValueChange = { holderName = it },
                label = { Text("Card Holder Name") },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = Zinc400) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Zinc800
                )
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = expiry,
                    onValueChange = { if (it.length <= 5) expiry = it },
                    label = { Text("Expiry (MM/YY)") },
                    leadingIcon = { Icon(Icons.Default.DateRange, null, tint = Zinc400) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Zinc800
                    )
                )

                OutlinedTextField(
                    value = cvv,
                    onValueChange = { if (it.length <= 3) cvv = it },
                    label = { Text("CVV") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Zinc400) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Zinc800
                    )
                )
            }
            
            // Error Message
            if (addCardState is AddCardResult.Error) {
                Text(
                    text = (addCardState as AddCardResult.Error).message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.validateAndAddCard(cardNumber, holderName, expiry, cvv)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = cardNumber.isNotEmpty() && holderName.isNotEmpty() && expiry.isNotEmpty() && cvv.isNotEmpty()
            ) {
                Text("Link Card")
            }
        }
    }
}
