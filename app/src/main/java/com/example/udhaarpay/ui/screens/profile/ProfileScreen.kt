package com.example.udhaarpay.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    var showEdit by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Amit Sharma") }
    var email by remember { mutableStateOf("amit.sharma@email.com") }
    var phone by remember { mutableStateOf("+91 98765 43210") }
    var address by remember { mutableStateOf("Mumbai, India") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Name: $name", fontSize = 18.sp)
                Text("Email: $email", fontSize = 16.sp)
                Text("Phone: $phone", fontSize = 16.sp)
                Text("Address: $address", fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { showEdit = true }, modifier = Modifier.align(Alignment.End)) { Text("Edit") }
            }
        }
        if (showEdit) {
            AlertDialog(
                onDismissRequest = { showEdit = false },
                title = { Text("Edit Profile") },
                text = {
                    Column {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
                        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirm = true
                        showEdit = false
                    }) { Text("Save") }
                },
                dismissButton = { TextButton(onClick = { showEdit = false }) { Text("Cancel") } }
            )
        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Profile Updated") },
                text = { Text("Your profile has been updated.") },
                confirmButton = { TextButton(onClick = { showConfirm = false }) { Text("OK") } }
            )
        }
    }
}
