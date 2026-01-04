
package com.udhaarpay.app

import com.udhaarpay.app.ui.screens.support.SupportScreen
import com.udhaarpay.app.ui.screens.reminders.RemindersScreen
import com.udhaarpay.app.ui.screens.offers.OffersScreen
import com.udhaarpay.app.ui.screens.transactions.TransactionsScreen
import com.udhaarpay.app.ui.screens.scanpay.ScanPayScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.SendToMobile
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.udhaarpay.app.ui.screens.investments.InvestmentsNavHost
import com.udhaarpay.app.ui.screens.creditcards.CreditCardScreen
import com.udhaarpay.app.ui.screens.bankaccounts.BankAccountScreen
import com.udhaarpay.app.ui.screens.insurance.InsuranceScreen
import com.udhaarpay.app.ui.screens.debt.DebtScreen
import com.udhaarpay.app.ui.screens.profile.ProfileScreen
import com.udhaarpay.app.ui.screens.wallet.WalletManagementScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("dashboard") {
                            UdhaarPayDashboard(
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }
                        composable("investments") { InvestmentsNavHost() }
                        composable("credit_card") { CreditCardScreen() }
                        composable("bank_accounts") { BankAccountScreen() }
                        composable("insurance") { InsuranceScreen() }
                        composable("debt") { DebtScreen() }
                        composable("profile") { ProfileScreen() }
                        composable("wallet_management") { WalletManagementScreen() }
                        composable("reminders") { RemindersScreen() }
                        composable("support") { SupportScreen() }
                        composable("transactions") { TransactionsScreen() }
                        composable("offers") { OffersScreen() }
                        composable("scan_pay") { ScanPayScreen() }
                    }
                }
            }
        }
    }
}

data class PaymentOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun UdhaarPayDashboard(modifier: Modifier = Modifier, onNavigate: (String) -> Unit) {
    val paymentOptions = listOf(
        PaymentOption(
            "investments",
            "Investments",
            "Manage and invest in assets",
            Icons.Default.AccountBalance,
            Color(0xFF22C55E)
        ),
        PaymentOption(
            "scan_pay",
            "Scan & Pay",
            "Scan QR or add from gallery",
            Icons.Default.Payment,
            Color(0xFF2563EB)
        ),
        PaymentOption(
            "send_money",
            "Send Money",
            "Transfer funds to friends & family",
            Icons.Default.SendToMobile,
            Color(0xFF2563EB)
        ),
        PaymentOption(
            "request_money",
            "Request Money",
            "Ask for payment from others",
            Icons.Default.AccountBalance,
            Color(0xFF7C3AED)
        ),
        PaymentOption(
            "add_money",
            "Add Money",
            "Load funds to your wallet",
            Icons.Default.LocalAtm,
            Color(0xFF059669)
        ),
        PaymentOption(
            "pay_bills",
            "Pay Bills",
            "Pay utilities & other services",
            Icons.Default.Payment,
            Color(0xFFDC2626)
        ),
        PaymentOption(
            "mobile_recharge",
            "Mobile Recharge",
            "Recharge mobile & data plans",
            Icons.Default.PhonelinkSetup,
            Color(0xFFF59E0B)
        ),
        PaymentOption(
            "cashback",
            "Cashback Offers",
            "View available cashback deals",
            Icons.Default.LocalOffer,
            Color(0xFF06B6D4)
        ),
        PaymentOption(
            "credit_card",
            "Credit Card",
            "Manage your credit cards",
            Icons.Default.CreditCard,
            Color(0xFFEC4899)
        ),
        PaymentOption(
            "groups",
            "Split Expenses",
            "Group payments & splitting",
            Icons.Default.Group,
            Color(0xFF8B5CF6)
        ),
        PaymentOption(
            "bank_accounts",
            "Bank Accounts",
            "View and manage bank accounts",
            Icons.Default.AccountBalance,
            Color(0xFF0EA5E9)
        ),
        PaymentOption(
            "insurance",
            "Insurance",
            "View and buy insurance policies",
            Icons.Default.LocalOffer,
            Color(0xFF6366F1)
        ),
        PaymentOption(
            "debt",
            "Debt",
            "View and pay debts",
            Icons.Default.Payment,
            Color(0xFFDC2626)
        ),
        PaymentOption(
            "profile",
            "Profile",
            "View and edit your profile",
            Icons.Default.Group,
            Color(0xFF7C3AED)
        ),
        PaymentOption(
            "wallet_management",
            "Wallets",
            "Manage multiple wallets",
            Icons.Default.LocalAtm,
            Color(0xFF059669)
        ),
        PaymentOption(
            "transactions",
            "Transactions",
            "View all your transaction history",
            Icons.Default.Payment,
            Color(0xFF0EA5E9)
        ),
        PaymentOption(
            "offers",
            "Offers",
            "Latest offers and rewards",
            Icons.Default.LocalOffer,
            Color(0xFFF59E0B)
        ),
        PaymentOption(
            "reminders",
            "Bill Reminders",
            "Never miss a bill payment",
            Icons.Default.PhonelinkSetup,
            Color(0xFF6366F1)
        ),
        PaymentOption(
            "support",
            "Support",
            "Get help & support",
            Icons.Default.Group,
            Color(0xFFDC2626)
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp)
                )
                .padding(24.dp)
        ) {
            Text(
                "UdhaarPay",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Your Digital Payment Solution",
                fontSize = 14.sp,
                color = Color(0xFFCBD5E1),
                modifier = Modifier.padding(top = 4.dp)
            )
            // Wallet Balance
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
                    .padding(top = 16.dp)
            ) {
                Column {
                    Text(
                        "Wallet Balance",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        "₹5,450.00",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        // Payment Options Grid
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(paymentOptions.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { option ->
                        PaymentOptionCard(
                            option = option,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    // Navigate to the appropriate screen
                                    when (option.id) {
                                        "investments" -> onNavigate("investments")
                                        "scan_pay" -> onNavigate("scan_pay")
                                        "credit_card" -> onNavigate("credit_card")
                                        "bank_accounts" -> onNavigate("bank_accounts")
                                        "insurance" -> onNavigate("insurance")
                                        "debt" -> onNavigate("debt")
                                        "profile" -> onNavigate("profile")
                                        "wallet_management" -> onNavigate("wallet_management")
                                        "transactions" -> onNavigate("transactions")
                                        "offers" -> onNavigate("offers")
                                        "reminders" -> onNavigate("reminders")
                                        "support" -> onNavigate("support")
                                        else -> {}
                                    }
                                }
                        )
                    }
                    // Add spacer for odd number of items
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentOptionCard(
    option: PaymentOption,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                tint = option.color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                option.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1
            )
            Text(
                option.description,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                maxLines = 2,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun PaymentDetailScreen(
    option: PaymentOption,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        // Back Button
        Text(
            "← Back",
            color = option.color,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable { onBack() }
                .padding(8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Feature Icon & Title
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = option.color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                tint = option.color,
                modifier = Modifier.size(60.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            option.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Text(
            option.description,
            fontSize = 14.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Feature List
        listOf(
            "Fast and secure transactions",
            "Real-time notifications",
            "Transaction history tracking",
            "24/7 customer support",
            "Multiple payment methods"
        ).forEach { feature ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(option.color, shape = RoundedCornerShape(50.dp))
                )
                Text(
                    feature,
                    fontSize = 14.sp,
                    color = Color(0xFFE2E8F0),
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Action Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(option.color, shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Get Started",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}