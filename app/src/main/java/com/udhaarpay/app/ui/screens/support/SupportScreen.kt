package com.udhaarpay.app.ui.screens.support

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
            .background(Color(0xFF0F172A))
            .padding(20.dp)
    ) {
        Text("Support & Help", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFFDC2626))
        Spacer(Modifier.height(12.dp))
        Text("FAQs", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
        mockFaqs.forEach { faq ->
            Text("• $faq", fontSize = 14.sp, color = Color(0xFFCBD5E1), modifier = Modifier.padding(vertical = 2.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Chat with Support", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF1E293B), shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
        ) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                chatHistory.forEach { msg ->
                    Text(msg, fontSize = 14.sp, color = Color(0xFFCBD5E1), modifier = Modifier.padding(vertical = 2.dp))
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
                    .background(Color(0xFF16213E), shape = MaterialTheme.shapes.small)
                    .padding(8.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (chatInput.isNotBlank()) {
                    chatHistory = chatHistory + "You: $chatInput" + "Support: We'll get back to you soon."
                    chatInput = ""
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))) {
                Text("Send", color = Color.White)
            }
        }
    }
}
