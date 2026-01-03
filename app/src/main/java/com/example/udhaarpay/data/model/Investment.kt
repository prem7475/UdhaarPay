package com.example.udhaarpay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class InvestmentType {
    DEMAT_ACCOUNT, SIP, MUTUAL_FUNDS, BONDS
}

data class InvestmentBroker(
    val name: String,
    val website: String,
    val types: List<InvestmentType>,
    val emoji: String
)

@Entity(tableName = "investments")
data class Investment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val type: InvestmentType,
    val broker: String,
    val visitDate: Long = System.currentTimeMillis()
)

val investmentBrokers = listOf(
    InvestmentBroker(
        "Zerodha",
        "https://www.zerodha.com",
        listOf(InvestmentType.DEMAT_ACCOUNT, InvestmentType.SIP, InvestmentType.MUTUAL_FUNDS),
        "📊"
    ),
    InvestmentBroker(
        "Groww",
        "https://www.groww.in",
        listOf(InvestmentType.MUTUAL_FUNDS, InvestmentType.SIP, InvestmentType.BONDS),
        "📈"
    ),
    InvestmentBroker(
        "Upstox",
        "https://www.upstox.com",
        listOf(InvestmentType.DEMAT_ACCOUNT, InvestmentType.MUTUAL_FUNDS),
        "💹"
    ),
    InvestmentBroker(
        "Angel One",
        "https://www.angelone.in",
        listOf(InvestmentType.DEMAT_ACCOUNT, InvestmentType.SIP),
        "🔔"
    ),
    InvestmentBroker(
        "ICICI Direct",
        "https://www.icic idirect.com",
        listOf(InvestmentType.DEMAT_ACCOUNT, InvestmentType.MUTUAL_FUNDS, InvestmentType.BONDS),
        "🏦"
    ),
    InvestmentBroker(
        "Paytm Money",
        "https://www.paytmmoney.com",
        listOf(InvestmentType.MUTUAL_FUNDS, InvestmentType.SIP, InvestmentType.BONDS),
        "💰"
    )
)
