package com.udhaarpay.app.ui.screens.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.ui.components.PremiumInfoCard
import com.udhaarpay.app.ui.components.PremiumScreen
import com.udhaarpay.app.ui.components.PremiumSectionHeader
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.components.UdhaarPayTextButton
import com.udhaarpay.app.ui.viewmodel.UserProfileViewModel

@Composable
fun SecurityCenterScreen(
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    var mpin by remember(currentUser?.userId) { mutableStateOf(currentUser?.mpin.orEmpty()) }
    var tpin by remember(currentUser?.userId) { mutableStateOf(currentUser?.tpin.orEmpty()) }
    val scrollState = rememberScrollState()

    PremiumScreen {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PremiumSectionHeader(
                title = "Security Center",
                subtitle = "Set app MPIN, transfer TPIN, and keep local controls in one place"
            )

            PremiumInfoCard {
                Text("Status", fontWeight = FontWeight.SemiBold)
                Text("App MPIN: ${if (currentUser?.mpin.isNullOrBlank()) "Not set" else "Set"}")
                Text("Bank TPIN: ${if (currentUser?.tpin.isNullOrBlank()) "Not set" else "Set"}")
                Text("UPI PIN: Set per bank account under Bank Accounts", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("RuPay NFC only works with RuPay cards", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            PremiumInfoCard {
                Text("Set MPIN / TPIN", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = mpin,
                    onValueChange = { mpin = it.filter(Char::isDigit).take(6) },
                    label = { Text("MPIN for Login") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = tpin,
                    onValueChange = { tpin = it.filter(Char::isDigit).take(6) },
                    label = { Text("TPIN for Bank Transfers") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    UdhaarPayButton(
                        text = "Save Security",
                        onClick = {
                            if (mpin.length in 4..6 && tpin.length in 4..6) {
                                viewModel.saveSecurityCredentials(mpin, tpin)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.74f)
                    )
                    UdhaarPayTextButton(
                        text = "Reset",
                        onClick = {
                            mpin = currentUser?.mpin.orEmpty()
                            tpin = currentUser?.tpin.orEmpty()
                        }
                    )
                }
            }

            if (!statusMessage.isNullOrBlank()) {
                PremiumInfoCard {
                    Text(statusMessage.orEmpty(), color = MaterialTheme.colorScheme.primary)
                    UdhaarPayTextButton(text = "Dismiss", onClick = { viewModel.clearStatusMessage() })
                }
            }

            PremiumInfoCard {
                Text("How It Works", fontWeight = FontWeight.SemiBold)
                Text("Use MPIN to unlock the app.")
                Text("Use TPIN for bank transfers and high-value payments.")
                Text("Use UPI PIN from the account card when paying through UPI.")
            }
        }
    }
}
