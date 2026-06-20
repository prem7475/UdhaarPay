package com.udhaarpay.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.core.MockDataSeeder
import com.udhaarpay.app.ui.screens.security.AppLockScreen
import com.udhaarpay.app.ui.theme.UdhaarPayBrushes
import com.udhaarpay.app.ui.viewmodel.UserProfileViewModel

@Composable
fun UdhaarPayAppRoot(seeder: MockDataSeeder) {
    val userViewModel: UserProfileViewModel = hiltViewModel()
    val currentUser by userViewModel.currentUser.collectAsState()
    var bootstrapped by remember { mutableStateOf(false) }
    var unlocked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        seeder.seedMockData()
        bootstrapped = true
    }

    when {
        !bootstrapped || currentUser == null -> LaunchingScreen()
        currentUser?.mpin.isNullOrBlank() -> UdhaarPayAppShell()
        unlocked -> UdhaarPayAppShell()
        else -> AppLockScreen(
            hintText = currentUser?.mpin,
            onUnlock = { unlocked = true },
            onContinueWithoutLock = { unlocked = true }
        )
    }
}

@Composable
private fun LaunchingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UdhaarPayBrushes.AppBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("UdhaarPay", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Luxury payments, debt tracking, paper trading, bookings, and NFC mock flows.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Preparing your local vault...",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
