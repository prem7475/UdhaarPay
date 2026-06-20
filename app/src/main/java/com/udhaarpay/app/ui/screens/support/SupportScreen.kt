package com.udhaarpay.app.ui.screens.support

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udhaarpay.app.ui.components.PremiumActionCard
import com.udhaarpay.app.ui.components.PremiumInfoCard
import com.udhaarpay.app.ui.components.PremiumPill
import com.udhaarpay.app.ui.components.PremiumScreen
import com.udhaarpay.app.ui.components.PremiumSectionHeader
import com.udhaarpay.app.ui.components.UdhaarPayButton

private data class FaqItem(
    val question: String,
    val answer: String
)

private val mockFaqs = listOf(
    FaqItem("How do I reset my UPI PIN?", "Go to Bank Accounts, open the account card, and tap Reset UPI PIN."),
    FaqItem("How do I set MPIN or TPIN?", "Open Security Center and save your app MPIN plus transfer TPIN."),
    FaqItem("How do NFC payments work?", "Pick a RuPay card, tap Ready To Tap, and complete the mock NFC flow."),
    FaqItem("Can I use this offline?", "Yes, the current version uses local storage and seeded mock data.")
)

@Composable
fun SupportScreen() {
    var message by remember { mutableStateOf("") }
    var chatHistory by remember {
        mutableStateOf(
            listOf(
                "Welcome to UdhaarPay Help Desk.",
                "We keep support, privacy, and app info in one calm place."
            )
        )
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
                title = "Help Desk",
                subtitle = "Support, policies, versions, and quick answers"
            )

            PremiumInfoCard {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        BoxCenter(Icons.Default.SupportAgent, MaterialTheme.colorScheme.primary)
                    }
                    Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                        Text("Live Support Queue", fontWeight = FontWeight.SemiBold)
                        Text("We answer mock support requests right inside the app.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                    PremiumPill(text = "Open", selected = true, onClick = {})
                }
            }

            PremiumActionCard(
                title = "App Version",
                subtitle = "UdhaarPay v1.0 mock build — local storage enabled",
                onClick = {},
                leadingContent = {
                    BoxBadge(Icons.Default.Info, MaterialTheme.colorScheme.secondary)
                },
                trailingContent = {
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            )

            PremiumActionCard(
                title = "Terms & Conditions",
                subtitle = "Usage, privacy, and data handling notes",
                onClick = {},
                leadingContent = {
                    BoxBadge(Icons.Default.Policy, MaterialTheme.colorScheme.primary)
                },
                trailingContent = {
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            )

            PremiumActionCard(
                title = "FAQ",
                subtitle = "Tap to read the most common questions",
                onClick = {},
                leadingContent = {
                    BoxBadge(Icons.Default.Help, MaterialTheme.colorScheme.secondary)
                },
                trailingContent = {
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            )

            PremiumSectionHeader(
                title = "Common Questions",
                subtitle = "Premium help that stays minimal and readable"
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                mockFaqs.forEach { faq ->
                    PremiumInfoCard {
                        Text(faq.question, fontWeight = FontWeight.SemiBold)
                        Text(
                            faq.answer,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            PremiumSectionHeader(
                title = "Contact Support",
                subtitle = "Leave a note and we keep the thread locally"
            )

            PremiumInfoCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it.take(160) },
                        label = { Text("Describe the issue") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    UdhaarPayButton(
                        text = "Send Support Note",
                        onClick = {
                            if (message.isNotBlank()) {
                                chatHistory = chatHistory + "You: $message" + "Support: Thanks, we have received your note."
                                message = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            PremiumInfoCard {
                Text("Support Thread", fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    chatHistory.forEach { entry ->
                        Text(
                            entry,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxBadge(icon: ImageVector, tint: Color) {
    BoxCenter(icon = icon, tint = tint)
}

@Composable
private fun BoxCenter(icon: ImageVector, tint: Color) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxWidth().height(44.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint)
    }
}
