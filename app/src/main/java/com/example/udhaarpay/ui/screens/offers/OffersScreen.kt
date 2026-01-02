package com.example.udhaarpay.ui.screens.offers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.udhaarpay.data.model.Offer
import com.example.udhaarpay.ui.components.PremiumTopAppBar
import com.example.udhaarpay.ui.theme.*
import com.example.udhaarpay.ui.viewmodel.OfferViewModel

@Composable
fun OffersScreen(
    onBack: () -> Unit,
    viewModel: OfferViewModel = hiltViewModel()
) {
    val offers by viewModel.allOffers.collectAsState(initial = emptyList())
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        viewModel.loadAllOffers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground)
    ) {
        PremiumTopAppBar(
            title = "Special Offers",
            onBackClick = onBack
        )

        // Category Tabs
        if (categories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = category
                                if (category == "All") {
                                    viewModel.loadAllOffers()
                                } else {
                                    viewModel.loadOffersByCategory(category)
                                }
                            },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonOrange,
                            selectedLabelColor = DarkBackground,
                            containerColor = DarkCard,
                            labelColor = TextSecondary
                        ),
                        border = if (selectedCategory == category) null else FilterChipDefaults.border(
                            borderColor = TextTertiary.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonOrange)
            }
        } else if (offers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = "No offers",
                        tint = TextTertiary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No offers available",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(offers) { offer ->
                    OfferCard(offer = offer)
                }
            }
        }
    }
}

@Composable
fun OfferCard(offer: Offer) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Background gradient fallback (category-based)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = when (offer.category.lowercase()) {
                                "food" -> listOf(NeonOrange, NeonOrangeDark)
                                "accessories" -> listOf(CardGradient2Start, CardGradient2End)
                                "electronics" -> listOf(CardGradient3Start, CardGradient3End)
                                "travel" -> listOf(Color(0xFF4CAF50), Color(0xFF66BB6A))
                                else -> listOf(CardGradient1Start, CardGradient1End)
                            }
                        )
                    )
            )

            // Content overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = offer.category.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextTertiary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = offer.discount,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = offer.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 2
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = offer.description,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1
                    )

                    // CTA pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(color = Color.White)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "CLAIM NOW",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = when (offer.category.lowercase()) {
                                "food" -> NeonOrange
                                "accessories" -> CardGradient2Start
                                "electronics" -> CardGradient3Start
                                "travel" -> Color(0xFF2E7D32)
                                else -> NeonOrange
                            }
                        )
                    }
                }
            }
        }
    }
}
