package com.example.udhaarpay.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.ui.components.*
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val otp by viewModel.otp.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val otpSent by viewModel.otpSent.collectAsState()
    val authSuccess by viewModel.authSuccess.collectAsState()

    LaunchedEffect(authSuccess) {
        if (authSuccess) {
            onAuthSuccess()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkCurrentUser()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo/Header
            Text(
                text = "UdhaarPay",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeonOrange,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Fast & Secure Payments",
                fontSize = 16.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Phone Input Section
            AnimatedVisibility(
                visible = !otpSent,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Text(
                        text = "Enter Your Phone Number",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 24.dp)
                    )

                    PremiumTextField(
                        value = phoneNumber,
                        onValueChange = { viewModel.setPhoneNumber(it) },
                        label = "Phone Number",
                        placeholder = "Enter 10-digit number",
                        keyboardType = KeyboardType.Number,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Phone",
                                tint = NeonOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "We'll send you an OTP to verify your number",
                        fontSize = 12.sp,
                        color = TextTertiary,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    PremiumButton(
                        text = "Send OTP",
                        onClick = { viewModel.sendOTP() },
                        isLoading = isLoading,
                        enabled = phoneNumber.length == 10 && !isLoading
                    )
                }
            }

            // OTP Input Section
            AnimatedVisibility(
                visible = otpSent,
                enter = slideInVertically() + fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Text(
                        text = "Verify with OTP",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp)
                    )

                    Text(
                        text = "We've sent a 6-digit code to +91${phoneNumber}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 24.dp)
                    )

                    // OTP Input Fields
                    OTPInputField(
                        value = otp,
                        onValueChange = { viewModel.setOTP(it) },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Didn't receive?",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Resend OTP",
                            fontSize = 12.sp,
                            color = NeonOrange,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { viewModel.sendOTP() }
                        )
                    }

                    PremiumButton(
                        text = "Verify & Continue",
                        onClick = { viewModel.verifyOTP() },
                        isLoading = isLoading,
                        enabled = otp.length == 6 && !isLoading,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    PremiumButton(
                        text = "Change Phone Number",
                        onClick = {
                            viewModel.resetAuth()
                        },
                        backgroundColor = DarkCard,
                        textColor = NeonOrange
                    )
                }
            }

            // Error Message
            if (error != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚠",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = error!!,
                            fontSize = 13.sp,
                            color = ErrorRed,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = ErrorRed,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { viewModel.clearError() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Footer
            Text(
                text = "By continuing, you agree to our Terms & Privacy Policy",
                fontSize = 11.sp,
                color = TextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        LoadingDialog(isVisible = isLoading, message = "Processing...")
    }
}

@Composable
fun OTPInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            OutlinedTextField(
                value = value.getOrNull(index)?.toString() ?: "",
                onValueChange = { newValue ->
                    if (newValue.length <= 1) {
                        val newOtp = value.toMutableList()
                        if (newValue.isNotEmpty()) {
                            if (newOtp.size <= index) {
                                newOtp.add(newValue[0])
                            } else {
                                newOtp[index] = newValue[0]
                            }
                        } else if (index < newOtp.size) {
                            newOtp.removeAt(index)
                        }
                        onValueChange(newOtp.joinToString(""))
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonOrange,
                    unfocusedBorderColor = TextTertiary.copy(alpha = 0.3f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )
        }
    }
}
