package com.udhaarpay.app.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.ui.viewmodel.UserProfileViewModel

@Composable
fun LinkUPIScreen(viewModel: UserProfileViewModel = hiltViewModel()) {
    val currentUser by viewModel.currentUser.collectAsState()
    val status by viewModel.statusMessage.collectAsState()
    var phone by remember(currentUser?.userId) { mutableStateOf(currentUser?.phone.orEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Link UPI To Phone", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it.filter { ch -> ch.isDigit() || ch == '+' }.take(13) },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { viewModel.linkUpiToPhone(phone) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Link UPI")
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Generated UPI", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            currentUser?.upiId ?: "Not linked",
            style = MaterialTheme.typography.titleMedium
        )
        if (!status.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(status ?: "", color = MaterialTheme.colorScheme.primary)
        }
    }
}
