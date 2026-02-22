package com.udhaarpay.app.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val mockFaqs = listOf(
    "How to reset my UPI PIN?",
    "How to add a new bank account?",
    "How to contact customer support?",
    "How to check cashback offers?"
)

@Composable
fun SupportScreen() {
    var chatInput by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf("Welcome to UdhaarPay Support. How can we help you?")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Text(
            "Support & Help",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.padding(12.dp))

        Text("FAQs", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
        mockFaqs.forEach { faq ->
            Text(
                "- $faq",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        Spacer(Modifier.padding(16.dp))
        Text("Chat with Support", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                chatHistory.forEach { msg ->
                    Text(
                        msg,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                modifier = Modifier
                    .weight(1f)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(8.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface)
            )
            Spacer(Modifier.padding(4.dp))
            Button(
                onClick = {
                    if (chatInput.isNotBlank()) {
                        chatHistory = chatHistory + "You: $chatInput" + "Support: We will get back to you soon."
                        chatInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Send", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
