package com.example.udhaarpay.ui.screens.services

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udhaarpay.ui.navigation.Route
import com.example.udhaarpay.ui.theme.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class ServiceItem(
    val name: String,
    val icon: ImageVector,
    val url: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onBack: () -> Unit,
    navController: NavController? = null,
    onNavigateToWeb: (String, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    
    // Bottom Sheet States
    var showInvestmentSheet by remember { mutableStateOf(false) }
    var showRechargeSheet by remember { mutableStateOf(false) }
    var selectedServiceTitle by remember { mutableStateOf("") }

    val billPayments = listOf(
        ServiceItem("Mobile", Icons.Rounded.PhoneAndroid),
        ServiceItem("DTH", Icons.Rounded.Tv),
        ServiceItem("Electricity", Icons.Rounded.ElectricBolt),
        ServiceItem("FastTag", Icons.Rounded.DirectionsCar)
    )

    val travelBooking = listOf(
        ServiceItem("Flight", Icons.Rounded.Flight, "https://www.skyscanner.co.in"),
        ServiceItem("Train", Icons.Rounded.Train, "https://www.irctc.co.in"),
        ServiceItem("Bus", Icons.Rounded.DirectionsBus, "https://www.redbus.in"),
        ServiceItem("Movie", Icons.Rounded.Movie, "https://in.bookmyshow.com")
    )

    val finance = listOf(
        ServiceItem("Invest", Icons.Rounded.TrendingUp),
        ServiceItem("Insurance", Icons.Rounded.HealthAndSafety),
        ServiceItem("Demat", Icons.Rounded.AccountBalance),
        ServiceItem("Loans", Icons.Rounded.MonetizationOn)
    )

    Scaffold(
        containerColor = PureBlack,
        topBar = {
            // Simple Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onBack)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Services",
                    style = MaterialTheme.typography.titleLarge,
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Bill Payments
                item(span = { GridItemSpan(4) }) {
                    SectionHeader("Bill Payments")
                }
                items(billPayments) { service ->
                    ServiceItemView(service) {
                        if (service.name == "Mobile") {
                            showRechargeSheet = true
                        } else {
                            Toast.makeText(context, "Coming Soon: ${service.name}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Section 2: Travel Booking
                item(span = { GridItemSpan(4) }) {
                    SectionHeader("Travel Booking")
                }
                items(travelBooking) { service ->
                    ServiceItemView(service) {
                        if (service.url != null) {
                            val encodedUrl = URLEncoder.encode(service.url, StandardCharsets.UTF_8.toString())
                            onNavigateToWeb(encodedUrl, service.name)
                        } else {
                            Toast.makeText(context, "Coming Soon: ${service.name}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Section 3: Finance
                item(span = { GridItemSpan(4) }) {
                    SectionHeader("Finance")
                }
                items(finance) { service ->
                    ServiceItemView(service) {
                        if (service.name == "Invest" || service.name == "Demat") {
                            selectedServiceTitle = if (service.name == "Invest") "Investment Platforms" else "Open Demat Account"
                            showInvestmentSheet = true
                        } else {
                            Toast.makeText(context, "Coming Soon: ${service.name}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Bottom padding
                item(span = { GridItemSpan(4) }) {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Investment Bottom Sheet
            if (showInvestmentSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showInvestmentSheet = false },
                    containerColor = DarkZinc
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .padding(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(selectedServiceTitle, style = MaterialTheme.typography.titleLarge, color = White)
                        
                        BottomSheetOption(
                            title = "Zerodha", 
                            subtitle = "Stocks, F&O, Direct Mutual Funds",
                            onClick = {
                                showInvestmentSheet = false
                                val url = URLEncoder.encode("https://zerodha.com", StandardCharsets.UTF_8.toString())
                                onNavigateToWeb(url, "Zerodha")
                            }
                        )
                        
                        BottomSheetOption(
                            title = "Groww", 
                            subtitle = "Stocks, Mutual Funds, IPOs",
                            onClick = {
                                showInvestmentSheet = false
                                val url = URLEncoder.encode("https://groww.in", StandardCharsets.UTF_8.toString())
                                onNavigateToWeb(url, "Groww")
                            }
                        )
                        
                        BottomSheetOption(
                            title = "Upstox", 
                            subtitle = "Invest in Stocks, IPOs & Mutual Funds",
                            onClick = {
                                showInvestmentSheet = false
                                // Placeholder URL or just close
                                Toast.makeText(context, "Opening Upstox...", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // Recharge Bottom Sheet
            if (showRechargeSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showRechargeSheet = false },
                    containerColor = DarkZinc
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .padding(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Select Operator", style = MaterialTheme.typography.titleLarge, color = White)
                        
                        BottomSheetOption(
                            title = "Jio", 
                            subtitle = "Prepaid & Postpaid Plans",
                            onClick = {
                                showRechargeSheet = false
                                val url = URLEncoder.encode("https://www.jio.com/selfcare/recharge", StandardCharsets.UTF_8.toString())
                                onNavigateToWeb(url, "Jio Recharge")
                            }
                        )
                        
                        BottomSheetOption(
                            title = "Airtel", 
                            subtitle = "Recharge Online",
                            onClick = {
                                showRechargeSheet = false
                                val url = URLEncoder.encode("https://www.airtel.in/recharge", StandardCharsets.UTF_8.toString())
                                onNavigateToWeb(url, "Airtel Recharge")
                            }
                        )
                        
                        BottomSheetOption(
                            title = "Vi", 
                            subtitle = "Vodafone Idea Recharge",
                            onClick = {
                                showRechargeSheet = false
                                Toast.makeText(context, "Vi Recharge Selected", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomSheetOption(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Zinc400, style = MaterialTheme.typography.bodySmall)
        }
        Icon(Icons.Rounded.ArrowForwardIos, contentDescription = null, tint = Zinc400, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = White,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun ServiceItemView(service: ServiceItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(DarkZinc, CircleShape)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = service.icon,
                contentDescription = service.name,
                tint = PrimaryBlue,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = service.name,
            style = MaterialTheme.typography.bodySmall,
            color = Zinc400,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}
