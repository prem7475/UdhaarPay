package com.example.udhaarpay.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.udhaarpay.data.model.User
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.udhaarpay.ui.components.*
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState(initial = null)
    val isEditing by viewModel.isEditing.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground)
    ) {
        PremiumTopAppBar(
            title = "Profile",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                // Profile Header
                ProfileHeaderSection(
                    user = user,
                    isEditing = isEditing,
                    onEditClick = { viewModel.setEditing(true) }
                )
            }

            item {
                if (isEditing) {
                    // Edit Form
                    EditProfileForm(
                        fullName = fullName,
                        onFullNameChange = { viewModel.setFullName(it) },
                        email = email,
                        onEmailChange = { viewModel.setEmail(it) },
                        phoneNumber = user?.phoneNumber ?: "",
                        onSave = { viewModel.updateProfile() },
                        onCancel = { viewModel.setEditing(false) },
                        isLoading = isLoading
                    )
                } else {
                    // View Profile Info
                    ViewProfileSection(user = user)
                }
            }

            item {
                Divider(color = TextTertiary.copy(alpha = 0.2f))
            }

            item {
                // Settings Section
                SettingItem(
                    icon = Icons.Default.Lock,
                    title = "Change Password",
                    subtitle = "Update your login password",
                    onClick = { }
                )
            }

            item {
                SettingItem(
                    icon = Icons.Default.NotificationsActive,
                    title = "Notifications",
                    subtitle = "Manage notification preferences",
                    onClick = { }
                )
            }

            item {
                SettingItem(
                    icon = Icons.Default.Security,
                    title = "Security & Privacy",
                    subtitle = "Review security settings",
                    onClick = { }
                )
            }

            item {
                SettingItem(
                    icon = Icons.Default.Help,
                    title = "Help & Support",
                    subtitle = "Get help with your account",
                    onClick = { }
                )
            }

            item {
                SettingItem(
                    icon = Icons.Default.Info,
                    title = "About UdhaarPay",
                    subtitle = "Version 1.0.0",
                    onClick = { }
                )
            }

            item {
                Divider(color = TextTertiary.copy(alpha = 0.2f))
            }

            item {
                // Logout Button
                PremiumButton(
                    text = "Logout",
                    onClick = onLogout,
                    backgroundColor = ErrorRed.copy(alpha = 0.2f),
                    textColor = ErrorRed
                )
            }

            item {
                // Error Display
                if (error != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = ErrorRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = error!!,
                                fontSize = 12.sp,
                                color = ErrorRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        LoadingDialog(isVisible = isLoading, message = "Updating profile...")
    }
}

@Composable
fun ProfileHeaderSection(
    user: User?,
    isEditing: Boolean,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Profile Picture
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(NeonOrange, NeonOrangeDark)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!user?.profileImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = user?.profileImageUrl,
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Text(
                    text = user?.name?.firstOrNull()?.toString() ?: "U",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Edit Icon
            if (!isEditing) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color = NeonOrange)
                        .clickable(onClick = onEditClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = DarkBackground,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Text(
            text = user?.name.orEmpty().ifEmpty { "User" },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = user?.email.orEmpty().ifEmpty { "No email added" },
            fontSize = 13.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun ViewProfileSection(user: User?) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Personal Information",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        // Full Name
        ProfileInfoCard(
            icon = Icons.Default.Person,
            label = "Full Name",
            value = user?.name.orEmpty().ifEmpty { "Not set" }
        )

        // Email
        ProfileInfoCard(
            icon = Icons.Default.Email,
            label = "Email",
            value = user?.email.orEmpty().ifEmpty { "Not set" }
        )

        // Phone Number
        ProfileInfoCard(
            icon = Icons.Default.Phone,
            label = "Phone Number",
            value = user?.phoneNumber.orEmpty().ifEmpty { "Not set" },
            isLocked = true,
            lockMessage = "Phone number cannot be changed as per security policy"
        )
    }
}

@Composable
fun ProfileInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    isLocked: Boolean = false,
    lockMessage: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = NeonOrange,
                    modifier = Modifier.size(20.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                    Text(
                        text = value,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = TextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (isLocked && lockMessage.isNotEmpty()) {
                Text(
                    text = lockMessage,
                    fontSize = 10.sp,
                    color = TextTertiary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun EditProfileForm(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phoneNumber: String,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isLoading: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Edit Profile",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        PremiumTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = "Full Name",
            placeholder = "Enter your full name",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Name",
                    tint = TextTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
        )

        PremiumTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email Address",
            placeholder = "Enter your email",
            keyboardType = KeyboardType.Email,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = TextTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
        )

        // Phone Number - Locked
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone",
                    tint = TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Phone Number",
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                    Text(
                        text = phoneNumber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = WarningOrange,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = "Phone number cannot be changed as per security policy.",
            fontSize = 10.sp,
            color = WarningOrange,
            modifier = Modifier.padding(top = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PremiumButton(
                text = "Cancel",
                onClick = onCancel,
                backgroundColor = DarkCard,
                textColor = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            PremiumButton(
                text = "Save",
                onClick = onSave,
                isLoading = isLoading,
                enabled = fullName.isNotEmpty() && email.isNotEmpty() && !isLoading,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = NeonOrange,
                modifier = Modifier.size(24.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
