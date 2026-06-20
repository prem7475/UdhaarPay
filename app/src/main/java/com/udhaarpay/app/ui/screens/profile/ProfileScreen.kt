package com.udhaarpay.app.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.udhaarpay.app.ui.components.PremiumActionCard
import com.udhaarpay.app.ui.components.PremiumInfoCard
import com.udhaarpay.app.ui.components.PremiumMetricCard
import com.udhaarpay.app.ui.components.PremiumScreen
import com.udhaarpay.app.ui.components.PremiumSectionHeader
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.components.UdhaarPayTextButton
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
    val scrollState = rememberScrollState()

    PremiumScreen {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PremiumSectionHeader(
                title = "Profile",
                subtitle = "Identity, linked accounts, and local profile controls"
            )

            PremiumInfoCard {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier.size(76.dp)
                    ) {
                        if (photoUrl.isNotBlank()) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Profile photo",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    (fullName.take(1).ifBlank { "U" }).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp
                                )
                            }
                        }
                    }
                    Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                        Text(fullName.ifBlank { "User Name" }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("UPI: ${user?.upiId ?: "Not linked"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Linked Banks: ${linkedBanks.size}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("Linked Cards: ${linkedCards.size}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                PremiumMetricCard(
                    title = "KYC",
                    value = if (user?.kycStatus == true) "Verified" else "Pending",
                    subtitle = "Local mock profile",
                    modifier = Modifier.weight(1f)
                )
                PremiumMetricCard(
                    title = "Security",
                    value = if (!user?.mpin.isNullOrBlank()) "Protected" else "Setup",
                    subtitle = "MPIN / TPIN",
                    modifier = Modifier.weight(1f)
                )
            }

            PremiumInfoCard {
                Text("Edit Profile", fontWeight = FontWeight.SemiBold)
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
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    UdhaarPayButton(
                        text = "Save Profile",
                        onClick = {
                            userViewModel.saveProfile(
                                fullName = fullName.trim(),
                                email = email.trim(),
                                phone = phone.trim(),
                                profilePhotoUrl = photoUrl.trim().ifBlank { null },
                                address = address.trim()
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    UdhaarPayButton(
                        text = "Photo",
                        onClick = { photoPicker.launch("image/*") },
                        modifier = Modifier.weight(0.42f)
                    )
                }
            }

            PremiumActionCard(
                title = "Security Center",
                subtitle = "Set MPIN, TPIN, and transfer controls",
                onClick = { /* Navigated by drawer */ }
            )

            if (!statusMessage.isNullOrBlank()) {
                PremiumInfoCard {
                    Text(statusMessage.orEmpty(), color = MaterialTheme.colorScheme.primary)
                    UdhaarPayTextButton(text = "Dismiss", onClick = { userViewModel.clearStatusMessage() })
                }
            }
        }
    }
}
