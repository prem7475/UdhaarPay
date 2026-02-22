package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.data.IndianMarketUniverse
import com.udhaarpay.app.data.local.entities.PaperTradingAccount
import com.udhaarpay.app.data.local.entities.Trade
import com.udhaarpay.app.data.model.HoldingItem
import com.udhaarpay.app.data.model.MarketQuote
import com.udhaarpay.app.data.model.PortfolioSummary
import com.udhaarpay.app.repository.MarketDataRepository
import com.udhaarpay.app.repository.PaperTradingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PaperTradingViewModel @Inject constructor(
    private val paperTradingRepository: PaperTradingRepository,
    private val marketDataRepository: MarketDataRepository
) : ViewModel() {

    private val _quotes = MutableStateFlow<Map<String, MarketQuote>>(emptyMap())
    val quotes: StateFlow<Map<String, MarketQuote>> = _quotes.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _selectedSymbol = MutableStateFlow<String?>(null)
    val selectedSymbol: StateFlow<String?> = _selectedSymbol.asStateFlow()

    private val _trackedSymbols = MutableStateFlow(IndianMarketUniverse.symbols)
    val trackedSymbols: StateFlow<List<String>> = _trackedSymbols.asStateFlow()

    val account: StateFlow<PaperTradingAccount?> = paperTradingRepository.observeAccount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val openTrades: StateFlow<List<Trade>> = paperTradingRepository.observeOpenTrades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val closedTrades: StateFlow<List<Trade>> = paperTradingRepository.observeClosedTrades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val holdings: StateFlow<List<HoldingItem>> = combine(openTrades, quotes) { trades, quoteMap ->
        trades.groupBy { it.stockSymbol }.map { (symbol, grouped) ->
            val quantity = grouped.sumOf { it.quantity }
            val weightedSum = grouped.sumOf { it.entryPrice * it.quantity }
            val avgEntry = if (quantity == 0) 0.0 else weightedSum / quantity
            val current = quoteMap[symbol]?.currentPrice ?: grouped.first().currentPrice
            val pnl = grouped.sumOf { it.profitLoss }
            HoldingItem(
                symbol = symbol,
                companyName = grouped.first().companyName,
                quantity = quantity,
                avgEntry = avgEntry,
                currentPrice = current,
                pnl = pnl
            )
        }.sortedByDescending { it.pnl }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val portfolioSummary: StateFlow<PortfolioSummary> = combine(account, openTrades, closedTrades) { acc, open, closed ->
        val cash = acc?.virtualBalance ?: 0.0
        val marketValueOpen = open.sumOf { it.currentPrice * it.quantity }
        val openPnl = open.sumOf { it.profitLoss }
        val closedPnl = closed.sumOf { it.profitLoss }
        val totalPnl = openPnl + closedPnl
        val todayPnl = calculateTodayPnl(open, closed)
        val invested = acc?.totalInvested ?: open.sumOf { it.entryPrice * it.quantity }

        PortfolioSummary(
            totalPortfolioValue = cash + marketValueOpen,
            totalProfitLoss = totalPnl,
            todayProfitLoss = todayPnl,
            totalInvested = invested
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PortfolioSummary(
            totalPortfolioValue = PaperTradingRepository.STARTING_BALANCE,
            totalProfitLoss = 0.0,
            todayProfitLoss = 0.0,
            totalInvested = 0.0
        )
    )

    val symbols = IndianMarketUniverse.symbols

    private val companies = IndianMarketUniverse.companies

    init {
        viewModelScope.launch {
            paperTradingRepository.ensureAccountExists()
        }
        startMarketFeed()
    }

    fun selectSymbol(symbol: String) {
        _selectedSymbol.value = symbol
    }

    fun clearSelectedSymbol() {
        _selectedSymbol.value = null
    }

    fun addCustomSymbol(raw: String) {
        val symbol = raw.trim().uppercase(Locale.getDefault())
        if (symbol.isBlank()) return
        if (_trackedSymbols.value.contains(symbol)) return
        _trackedSymbols.value = _trackedSymbols.value + symbol
    }

    fun buy(symbol: String, quantity: Int) {
        executeOpenTrade(symbol, quantity, PaperTradingRepository.TYPE_BUY)
    }

    fun call(symbol: String, quantity: Int) {
        executeOpenTrade(symbol, quantity, PaperTradingRepository.TYPE_CALL)
    }

    fun put(symbol: String, quantity: Int) {
        executeOpenTrade(symbol, quantity, PaperTradingRepository.TYPE_PUT)
    }

    fun sell(symbol: String) {
        val quote = _quotes.value[symbol]
        if (quote == null) {
            _statusMessage.value = "Price unavailable for $symbol"
            return
        }

        viewModelScope.launch {
            val result = paperTradingRepository.closeOpenBuyBySell(
                symbol = symbol,
                currentPrice = quote.currentPrice
            )
            result.onSuccess {
                _statusMessage.value = "SELL executed for $symbol. P&L INR ${"%.2f".format(it.profitLoss)}"
            }.onFailure {
                _statusMessage.value = it.message ?: "Unable to execute SELL"
            }
        }
    }

    fun closeTrade(tradeId: Long, symbol: String) {
        val quote = _quotes.value[symbol]
        if (quote == null) {
            _statusMessage.value = "Price unavailable for $symbol"
            return
        }
        viewModelScope.launch {
            val result = paperTradingRepository.closeTrade(tradeId, quote.currentPrice)
            result.onSuccess {
                _statusMessage.value = "Trade closed. P&L INR ${"%.2f".format(it.profitLoss)}"
            }.onFailure {
                _statusMessage.value = it.message ?: "Unable to close trade"
            }
        }
    }

    fun resetAccount() {
        viewModelScope.launch {
            val result = paperTradingRepository.resetAccount()
            result.onSuccess {
                _statusMessage.value = "Paper trading account reset to INR 1,00,000"
            }.onFailure {
                _statusMessage.value = it.message ?: "Reset failed"
            }
        }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }

    private fun executeOpenTrade(symbol: String, quantity: Int, type: String) {
        val quote = _quotes.value[symbol]
        if (quote == null) {
            _statusMessage.value = "Price unavailable for $symbol"
            return
        }
        viewModelScope.launch {
            val result = paperTradingRepository.placeOpenTrade(
                symbol = symbol,
                companyName = companies[symbol] ?: symbol,
                type = type,
                quantity = quantity,
                price = quote.currentPrice
            )
            result.onSuccess {
                _statusMessage.value = "$type executed: $quantity x $symbol @ INR ${"%.2f".format(quote.currentPrice)}"
            }.onFailure {
                _statusMessage.value = it.message ?: "Trade failed"
            }
        }
    }

    private fun startMarketFeed() {
        viewModelScope.launch {
            _trackedSymbols.collectLatest { liveSymbols ->
                marketDataRepository.observeQuotes(liveSymbols).collect { list ->
                    val quoteMap = list.associateBy { it.symbol }
                    _quotes.value = quoteMap
                    paperTradingRepository.syncOpenTradesWithQuotes(quoteMap)
                }
            }
        }
    }

    private fun calculateTodayPnl(open: List<Trade>, closed: List<Trade>): Double {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dayStart = calendar.timeInMillis

        val closedToday = closed.filter { it.timestamp >= dayStart }.sumOf { it.profitLoss }
        val openLive = open.sumOf { it.profitLoss }
        return closedToday + openLive
    }

    fun quoteFor(symbol: String): MarketQuote? = _quotes.value[symbol]
}
