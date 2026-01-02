package com.example.udhaarpay.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.udhaarpay.ui.theme.*

// Premium Button
@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = NeonOrange,
    textColor: Color = Color.White,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f),
            contentColor = textColor
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = textColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// Card Background Gradient
@Composable
fun CardWithGradientBackground(
    modifier: Modifier = Modifier,
    startColor: Color,
    endColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(startColor, endColor)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        content()
    }
}

// NOTE: The ImageVector-to-lambda compatibility helper is provided via the
// `CommonComponents` object below to avoid overload ambiguities. Keep the
// primary implementation accepting composable lambdas only.

// Premium Text Field
@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String = "",
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextTertiary,
                    fontSize = 14.sp
                )
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            singleLine = singleLine,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonOrange,
                unfocusedBorderColor = TextTertiary.copy(alpha = 0.3f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                errorBorderColor = ErrorRed,
                disabledBorderColor = TextTertiary.copy(alpha = 0.1f)
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
        )

        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// Backwards-compatible overload that accepts ImageVector for callers that still pass icons directly.
@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String = "",
    leadingIconImage: ImageVector? = null,
    trailingIconImage: ImageVector? = null,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    val leading: (@Composable (() -> Unit))? = leadingIconImage?.let { { Icon(imageVector = it, contentDescription = null) } }
    val trailing: (@Composable (() -> Unit))? = trailingIconImage?.let { { Icon(imageVector = it, contentDescription = null) } }
    PremiumTextField(value, onValueChange, modifier, placeholder, label, leading, trailing, singleLine, keyboardType, enabled, isError, errorMessage)
}

// Blurred Background Box
@Composable
fun BlurredBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = DarkCard.copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        content()
    }
}

// Premium Card
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        content()
    }
}

// Loading Dialog
@Composable
fun LoadingDialog(
    isVisible: Boolean = true,
    message: String = "Loading..."
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = OverlayDark),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = DarkCard,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = NeonOrange,
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Error Dialog
@Composable
fun ErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            if (onRetry != null) {
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonOrange)
                ) {
                    Text("Retry", color = Color.White)
                }
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DarkCard)
            ) {
                Text("Dismiss", color = NeonOrange)
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

// Success Dialog
@Composable
fun SuccessDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    actionText: String = "Continue"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = SuccessGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Text(actionText, color = Color.White)
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

// Top App Bar
@Composable
fun PremiumTopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = if (onBackClick != null) {
            {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            }
        } else null,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBackground,
            navigationIconContentColor = TextPrimary,
            titleContentColor = TextPrimary,
            actionIconContentColor = TextPrimary
        )
    )
}

// Shimmer Loading Effect
@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = DarkCard.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
    )
}

// Backwards-compatible object API used throughout the codebase.
object CommonComponents {
    @Composable
    fun PremiumButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, backgroundColor: Color = NeonOrange, textColor: Color = Color.White, isLoading: Boolean = false) {
        com.example.udhaarpay.ui.components.PremiumButton(text, onClick, modifier, enabled, backgroundColor, textColor, isLoading)
    }

    @Composable
    fun CardWithGradientBackground(modifier: Modifier = Modifier, startColor: Color, endColor: Color, content: @Composable () -> Unit) {
        com.example.udhaarpay.ui.components.CardWithGradientBackground(modifier, startColor, endColor, content)
    }

    @Composable
    fun PremiumTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        placeholder: String = "",
        label: String = "",
        leadingIcon: (@Composable (() -> Unit))? = null,
        trailingIcon: (@Composable (() -> Unit))? = null,
        singleLine: Boolean = true,
        keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
        enabled: Boolean = true,
        isError: Boolean = false,
        errorMessage: String = ""
    ) {
        com.example.udhaarpay.ui.components.PremiumTextField(value, onValueChange, modifier, placeholder, label, leadingIcon, trailingIcon, singleLine, keyboardType, enabled, isError, errorMessage)
    }

    @Composable
    fun PremiumTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        placeholder: String = "",
        label: String = "",
        leadingIconImage: ImageVector? = null,
        trailingIconImage: ImageVector? = null,
        singleLine: Boolean = true,
        keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
        enabled: Boolean = true,
        isError: Boolean = false,
        errorMessage: String = ""
    ) {
        com.example.udhaarpay.ui.components.PremiumTextField(value, onValueChange, modifier, placeholder, label, leadingIconImage, trailingIconImage, singleLine, keyboardType, enabled, isError, errorMessage)
    }

    @Composable
    fun BlurredBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
        com.example.udhaarpay.ui.components.BlurredBox(modifier, content)
    }

    @Composable
    fun PremiumCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
        com.example.udhaarpay.ui.components.PremiumCard(modifier, onClick, content)
    }

    @Composable
    fun LoadingDialog(isVisible: Boolean = true, message: String = "Loading...") {
        com.example.udhaarpay.ui.components.LoadingDialog(isVisible, message)
    }

    @Composable
    fun ErrorDialog(title: String, message: String, onDismiss: () -> Unit, onRetry: (() -> Unit)? = null) {
        com.example.udhaarpay.ui.components.ErrorDialog(title, message, onDismiss, onRetry)
    }

    @Composable
    fun SuccessDialog(title: String, message: String, onDismiss: () -> Unit, actionText: String = "Continue") {
        com.example.udhaarpay.ui.components.SuccessDialog(title, message, onDismiss, actionText)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PremiumTopAppBar(title: String, onBackClick: (() -> Unit)? = null, actions: @Composable (RowScope.() -> Unit)? = null) {
        com.example.udhaarpay.ui.components.PremiumTopAppBar(title, onBackClick, actions ?: {})
    }

    @Composable
    fun ShimmerBox(modifier: Modifier = Modifier) {
        com.example.udhaarpay.ui.components.ShimmerBox(modifier)
    }
}
