package com.udhaarpay.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    // Headlines (e.g., "Total Balance", "Welcome")
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default, // Or replace with GoogleFonts.Inter
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = White
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = White
    ),
    // Standard Body Text
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Zinc400
    ),
    // Labels (e.g., "Transaction Date")
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = Zinc500
    )
)