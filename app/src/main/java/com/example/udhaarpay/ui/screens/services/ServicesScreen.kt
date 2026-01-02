package com.example.udhaarpay.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.ui.components.PremiumTopAppBar
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.icons.*
import com.example.udhaarpay.ui.viewmodel.ServiceViewModel

data class ServiceCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val services: List<ServiceItem>
)

data class ServiceItem(
    val icon: ImageVector,
    val name: String,
    val description: String
)

@Composable
fun ServicesScreen(
    onBack: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    var selectedCategory by remember { mutableStateOf(0) }

    val serviceCategories = listOf(
        ServiceCategory(
            name = "Recharge & Bills",
            icon = Icons.Default.PhoneAndroid,
            color = NeonOrange,
            services = listOf(
                ServiceItem(PhoneAndroid, "Mobile Recharge", "Prepaid & Postpaid"),
                ServiceItem(ElectricBolt, "Electricity Bill", "Pay online instantly"),
                ServiceItem(Wifi, "Broadband", "Landline & Internet"),
                ServiceItem(Droplet, "Water Bill", "Municipal bills"),
                ServiceItem(LocalFireDepartment, "Gas Cylinder", "Home delivery booking"),
                ServiceItem(Tv, "DTH/Cable", "Recharge instantly")
            )
        ),
        ServiceCategory(
            name = "Travel & Bookings",
            icon = Icons.Default.Flight,
            color = AccentBlue,
            services = listOf(
                ServiceItem(Flight, "Flight Booking", "Domestic & International"),
                ServiceItem(Train, "Train Ticket", "IRCTC & more"),
                ServiceItem(DirectionsBus, "Bus Ticket", "Across India"),
                ServiceItem(Hotel, "Hotel Booking", "Best deals"),
                ServiceItem(Movie, "Movie Tickets", "All theatres")
            )
        ),
        ServiceCategory(
            name = "Finance & Investing",
            icon = Icons.Default.TrendingUp,
            color = SuccessGreen,
            services = listOf(
                ServiceItem(AccountBalance, "Demat Account", "Open free account"),
                ServiceItem(TrendingUp, "Mutual Funds", "Invest wisely"),
                ServiceItem(Savings, "SIP Investments", "Start investing"),
                ServiceItem(Security, "Insurance", "Protect family"),
                ServiceItem(Loan, "Loans", "Get approved"),
                ServiceItem(CreditScore, "Credit Score", "Check instantly")
            )
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground)
    ) {
        PremiumTopAppBar(
            title = "Services",
            onBackClick = onBack
        )

        // Category Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(serviceCategories.size) { index ->
                FilterChip(
                    selected = selectedCategory == index,
                    onClick = { selectedCategory = index },
                    label = {
                        Text(
                            text = serviceCategories[index].name,
                            fontSize = 12.sp,
                            fontWeight = if (selectedCategory == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = serviceCategories[index].icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonOrange,
                        selectedLabelColor = DarkBackground,
                        containerColor = DarkCard,
                        labelColor = TextSecondary,
                        selectedLeadingIconColor = DarkBackground,
                        leadingIconColor = TextTertiary
                    ),
                    border = if (selectedCategory == index) null else FilterChipDefaults.border(
                        borderColor = TextTertiary.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Services Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            val currentCategory = serviceCategories[selectedCategory]
            items(currentCategory.services) { service ->
                ServiceGridCard(
                    icon = service.icon,
                    name = service.name,
                    description = service.description,
                    color = currentCategory.color,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
fun ServiceGridCard(
    icon: ImageVector,
    name: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color.copy(alpha = 0.15f),
                            color.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color = color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = description,
                        fontSize = 10.sp,
                        color = TextTertiary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
