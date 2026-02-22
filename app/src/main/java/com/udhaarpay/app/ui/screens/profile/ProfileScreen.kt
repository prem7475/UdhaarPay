package com.udhaarpay.app.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.udhaarpay.app.ui.viewmodel.BankAccountViewModel
import com.udhaarpay.app.ui.viewmodel.CreditCardViewModel
import com.udhaarpay.app.ui.viewmodel.UserProfileViewModel

@Composable
fun ProfileScreen(
    userViewModel: UserProfileViewModel = hiltViewModel(),
    bankAccountViewModel: BankAccountViewModel = hiltViewModel(),
    creditCardViewModel: CreditCardViewModel = hiltViewModel()
) {
    val user by userViewModel.currentUser.collectAsState()
    val statusMessage by userViewModel.statusMessage.collectAsState()
    val linkedBanks by bankAccountViewModel.accounts.collectAsState()
    val linkedCards by creditCardViewModel.creditCards.collectAsState()

    var fullName by remember(user?.userId) { mutableStateOf(user?.fullName.orEmpty()) }
    var email by remember(user?.userId) { mutableStateOf(user?.email.orEmpty()) }
    var phone by remember(user?.userId) { mutableStateOf(user?.phone.orEmpty()) }
    var address by remember(user?.userId) { mutableStateOf(user?.address.orEmpty()) }
    var photoUrl by remember(user?.userId) { mutableStateOf(user?.profilePhotoUrl.orEmpty()) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) photoUrl = uri.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                if (photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            (fullName.take(1).ifBlank { "U" }).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                }
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(fullName.ifBlank { "User Name" }, style = MaterialTheme.typography.titleLarge)
                Text("UPI: ${user?.upiId ?: "Not linked"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Linked Banks: ${linkedBanks.size} | Linked Cards: ${linkedCards.size}")
            }
        }

        TextButton(onClick = { photoPicker.launch("image/*") }) {
            Text("Change Profile Photo")
        }

        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        userViewModel.saveProfile(
                            fullName = fullName.trim(),
                            email = email.trim(),
                            phone = phone.trim(),
                            profilePhotoUrl = photoUrl.trim().ifBlank { null },
                            address = address.trim()
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Profile")
                }
            }
        }

        if (!statusMessage.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(statusMessage ?: "", color = MaterialTheme.colorScheme.primary)
            TextButton(onClick = { userViewModel.clearStatusMessage() }) {
                Text("Dismiss")
            }
        }
    }
}
