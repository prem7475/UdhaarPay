package com.example.udhaarpay.ui.screens.investments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val governmentBonds = listOf(
    Bond("10-Year Govt Bond", 7.1, "AAA", "2026-12-31"),
    Bond("5-Year Govt Bond", 6.8, "AAA", "2029-12-31")
)
val corporateBonds = listOf(
    Bond("Reliance Corporate Bond", 8.2, "AA+", "2028-06-30"),
    Bond("TCS Corporate Bond", 7.9, "AA", "2027-03-31"),
    Bond("HDFC Bank Bond", 8.0, "AA", "2029-09-30")
)
data class Bond(val name: String, val interest: Double, val rating: String, val maturity: String)

@Composable
fun BondsScreen() {
    var selectedBond by remember { mutableStateOf<Bond?>(null) }
    var showConfirm by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text("Government Bonds", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        LazyColumn {
            items(governmentBonds) { bond ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { selectedBond = bond },
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(bond.name, fontWeight = FontWeight.Bold)
                        Text("Interest: ${bond.interest}%  |  Rating: ${bond.rating}")
                        Text("Maturity: ${bond.maturity}")
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Corporate Bonds", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        LazyColumn {
            items(corporateBonds) { bond ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { selectedBond = bond },
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(bond.name, fontWeight = FontWeight.Bold)
                        Text("Interest: ${bond.interest}%  |  Rating: ${bond.rating}")
                        Text("Maturity: ${bond.maturity}")
                    }
                }
            }
        }
        if (selectedBond != null) {
            AlertDialog(
                onDismissRequest = { selectedBond = null },
                title = { Text(selectedBond!!.name) },
                text = { Text("Interest: ${selectedBond!!.interest}%\nRating: ${selectedBond!!.rating}\nMaturity: ${selectedBond!!.maturity}\n\nWould you like to invest?") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirm = true
                        selectedBond = null
                    }) { Text("Invest") }
                },
                dismissButton = { TextButton(onClick = { selectedBond = null }) { Text("Cancel") } }
            )
        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Investment Successful") },
                text = { Text("Your investment in the bond has been placed.") },
                confirmButton = { TextButton(onClick = { showConfirm = false }) { Text("OK") } }
            )
        }
    }
}
