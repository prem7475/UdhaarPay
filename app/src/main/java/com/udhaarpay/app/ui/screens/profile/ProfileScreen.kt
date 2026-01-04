package com.udhaarpay.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

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
            .background(Color(0xFF0F172A))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 8.dp,
            color = Color(0xFF1E293B),
            modifier = Modifier.size(110.dp)
        ) {
            // Placeholder for user photo
            Box(Modifier.fillMaxSize())
        }
        Spacer(Modifier.height(18.dp))
        Text(name, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
        Text(email, fontSize = 15.sp, color = Color(0xFFCBD5E1))
        Text(phone, fontSize = 15.sp, color = Color(0xFFCBD5E1))
        Text(address, fontSize = 15.sp, color = Color(0xFFCBD5E1))
        Spacer(Modifier.height(18.dp))
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E)),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Profile Details", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text("Name: $name", fontSize = 16.sp, color = Color(0xFFCBD5E1))
                Text("Email: $email", fontSize = 15.sp, color = Color(0xFFCBD5E1))
                Text("Phone: $phone", fontSize = 15.sp, color = Color(0xFFCBD5E1))
                Text("Address: $address", fontSize = 15.sp, color = Color(0xFFCBD5E1))
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { showEdit = true },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                ) { Text("Edit", color = Color.White) }
            }
        }
        // Edit Profile Dialog
        if (showEdit) {
            Dialog(onDismissRequest = { showEdit = false }) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    tonalElevation = 12.dp,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.width(340.dp)
                ) {
                    Column(
                        Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                        Spacer(Modifier.height(18.dp))
                        // User photo placeholder
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF16213E),
                            border = BorderStroke(2.dp, Color(0xFF6366F1)),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(Modifier.fillMaxSize())
                        }
                        Spacer(Modifier.height(18.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name", color = Color(0xFFCBD5E1)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                cursorColor = Color(0xFF6366F1)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", color = Color(0xFFCBD5E1)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                cursorColor = Color(0xFF6366F1)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone", color = Color(0xFFCBD5E1)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                cursorColor = Color(0xFF6366F1)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address", color = Color(0xFFCBD5E1)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                cursorColor = Color(0xFF6366F1)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(22.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showEdit = false }) {
                                Text("Cancel", color = Color(0xFFCBD5E1))
                            }
                            Spacer(Modifier.width(10.dp))
                            Button(
                                onClick = { showEdit = false; showConfirm = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                            ) {
                                Text("Save", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
        // Confirmation Dialog
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                confirmButton = {
                    Button(onClick = { showConfirm = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))) {
                        Text("OK", color = Color.White)
                    }
                },
                title = { Text("Profile Updated", color = Color.White) },
                text = { Text("Your profile has been updated successfully.", color = Color(0xFFCBD5E1)) },
                containerColor = Color(0xFF1E293B)
            )
        }
    }
}