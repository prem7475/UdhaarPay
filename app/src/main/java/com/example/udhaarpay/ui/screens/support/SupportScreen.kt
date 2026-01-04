package com.example.udhaarpay.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val mockFaqs = listOf(
    "How to reset my UPI PIN?",
    "How to add a new bank account?",
    "How to contact customer support?",
    "How to check cashback offers?"
)

@Composable
fun SupportScreen() {
    var chatInput by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf("Welcome to UdhaarPay Support! How can we help you?")) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Support & Help", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFFDC2626))
        Spacer(Modifier.height(12.dp))
        Text("FAQs", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        mockFaqs.forEach { faq ->
            Text("• $faq", fontSize = 14.sp, color = Color(0xFF64748B), modifier = Modifier.padding(vertical = 2.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Chat with Support", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
        ) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                chatHistory.forEach { msg ->
                    Text(msg, fontSize = 14.sp, color = Color(0xFF334155), modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF1F5F9), shape = MaterialTheme.shapes.small)
                    .padding(8.dp),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (chatInput.isNotBlank()) {
                    chatHistory = chatHistory + "You: $chatInput" + "Support: We'll get back to you soon."
                    chatInput = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}
