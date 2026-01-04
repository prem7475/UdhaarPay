package com.example.udhaarpay.ui.screens.invest

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.udhaarpay.data.model.investmentBrokers
import com.example.udhaarpay.data.model.InvestmentType
import com.example.udhaarpay.ui.viewmodel.InvestmentViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentScreen(navController: NavHostController?, viewModel: InvestmentViewModel = hiltViewModel()) {
    var step by remember { mutableStateOf(0) }
    var selectedBroker by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<InvestmentType?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when (step) {
            0 -> {
                Text(text = "Select Broker", modifier = Modifier.padding(8.dp))
                investmentBrokers.forEach { b ->
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            selectedBroker = b.name
                            step = 1
                        }, shape = RoundedCornerShape(8.dp)) {
                        Box(modifier = Modifier.padding(12.dp)) { Text(text = b.name) }
                    }
                }
            }
            1 -> {
                Text(text = "Select Investment Type for: ${selectedBroker}", modifier = Modifier.padding(8.dp))
                val broker = investmentBrokers.find { it.name == selectedBroker }
                broker?.types?.forEach { t ->
                    Button(onClick = { selectedType = t; step = 2 }, modifier = Modifier.padding(vertical = 6.dp)) {
                        Text(text = t.name)
                    }
                }
            }
            2 -> {
                Text(text = "Confirm Investment", modifier = Modifier.padding(8.dp))
                Text(text = "Broker: $selectedBroker")
                Text(text = "Type: ${selectedType?.name}")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = { step = 1 }) { Text("Back") }
                    Button(onClick = {
                        if (selectedBroker != null && selectedType != null) {
                            viewModel.addInvestment(userId = 1L, type = selectedType!!, broker = selectedBroker!!)
                        }
                        navController?.navigateUp()
                    }) { Text("Confirm & Invest") }
                }
            }
        }
    }
}
