package com.udhaarpay.app.ui.screens.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udhaarpay.app.ui.theme.UdhaarPayBrushes

@Composable
fun AppLockScreen(
    hintText: String?,
    onUnlock: () -> Unit,
    onContinueWithoutLock: () -> Unit
) {
    var mpin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UdhaarPayBrushes.AppBackground)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Welcome to UdhaarPay", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Secure entry with a private MPIN.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Demo hint: ${hintText ?: "No MPIN set yet"}",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(2.dp))
                OutlinedTextField(
                    value = mpin,
                    onValueChange = {
                        mpin = it.filter(Char::isDigit).take(6)
                        error = null
                    },
                    label = { Text("Enter MPIN") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                if (!error.isNullOrBlank()) {
                    Text(error.orEmpty(), color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
                Button(
                    onClick = {
                        if (mpin.isBlank()) {
                            error = "Enter your MPIN."
                        } else {
                            onUnlock()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Unlock App")
                }
                TextButton(
                    onClick = onContinueWithoutLock,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue without lock")
                }
            }
        }
    }
}
