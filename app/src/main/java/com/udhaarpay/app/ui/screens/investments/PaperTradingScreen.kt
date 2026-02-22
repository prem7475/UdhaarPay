package com.udhaarpay.app.ui.screens.investments

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.udhaarpay.app.data.IndianMarketUniverse
import com.udhaarpay.app.data.local.entities.Trade
import com.udhaarpay.app.data.model.HoldingItem
import com.udhaarpay.app.data.model.MarketQuote
import com.udhaarpay.app.data.model.PortfolioSummary
import com.udhaarpay.app.repository.PaperTradingRepository
import com.udhaarpay.app.ui.components.UdhaarPayButton
import com.udhaarpay.app.ui.components.UdhaarPayTextButton
import com.udhaarpay.app.ui.theme.LossRed
import com.udhaarpay.app.ui.theme.ProfitGreen
import com.udhaarpay.app.ui.viewmodel.PaperTradingViewModel
import kotlin.math.max
import kotlin.math.min

private enum class MarketSegment(val label: String) {
    Equity("Equity"),
    Futures("Futures"),
    Options("Options")
}

@Composable
fun PaperTradingScreen(viewModel: PaperTradingViewModel = hiltViewModel()) {
    val selectedSymbol by viewModel.selectedSymbol.collectAsState()
    val quotes by viewModel.quotes.collectAsState()
    val account by viewModel.account.collectAsState()
    val openTrades by viewModel.openTrades.collectAsState()
    val closedTrades by viewModel.closedTrades.collectAsState()
    val holdings by viewModel.holdings.collectAsState()
    val summary by viewModel.portfolioSummary.collectAsState()
    val status by viewModel.statusMessage.collectAsState()
    val trackedSymbols by viewModel.trackedSymbols.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    if (selectedSymbol != null) {
        StockDetailScreen(
            symbol = selectedSymbol.orEmpty(),
            quote = quotes[selectedSymbol.orEmpty()],
            status = status,
            onBack = { viewModel.clearSelectedSymbol() },
            onBuy = { qty -> viewModel.buy(selectedSymbol.orEmpty(), qty) },
            onSell = { viewModel.sell(selectedSymbol.orEmpty()) },
            onCall = { qty -> viewModel.call(selectedSymbol.orEmpty(), qty) },
            onPut = { qty -> viewModel.put(selectedSymbol.orEmpty(), qty) },
            onDismissStatus = { viewModel.clearStatus() }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF050608), Color(0xFF0A0D13), Color(0xFF12161D))
                    )
                )
                .padding(12.dp)
        ) {
            AccountSummaryHeader(
                accountBalance = account?.virtualBalance ?: PaperTradingRepository.STARTING_BALANCE,
                summary = summary
            )
            Spacer(Modifier.height(10.dp))
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Market") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Portfolio") })
            }
            Spacer(Modifier.height(8.dp))
            if (selectedTab == 0) {
                TradingHomeScreen(
                    symbols = trackedSymbols,
                    quotes = quotes,
                    onOpenStock = { symbol ->
                        viewModel.addCustomSymbol(symbol)
                        viewModel.selectSymbol(symbol)
                    }
                )
            } else {
                PortfolioScreen(
                    summary = summary,
                    holdings = holdings,
                    openTrades = openTrades,
                    closedTrades = closedTrades,
                    onCloseTrade = { trade -> viewModel.closeTrade(trade.tradeId, trade.stockSymbol) },
                    onResetAccount = { viewModel.resetAccount() }
                )
            }
            if (!status.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(status ?: "", color = MaterialTheme.colorScheme.primary)
                UdhaarPayTextButton(text = "Dismiss", onClick = { viewModel.clearStatus() })
            }
        }
    }
}

@Composable
private fun AccountSummaryHeader(
    accountBalance: Double,
    summary: PortfolioSummary
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text("Paper Trading Balance", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "INR ${"%,.2f".format(accountBalance)}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(6.dp))
            Text("Portfolio Value: INR ${"%,.2f".format(summary.totalPortfolioValue)}")
            Text(
                "Total P&L: INR ${"%,.2f".format(summary.totalProfitLoss)}",
                color = if (summary.totalProfitLoss >= 0) ProfitGreen else LossRed
            )
        }
    }
}

@Composable
private fun TradingHomeScreen(
    symbols: List<String>,
    quotes: Map<String, MarketQuote>,
    onOpenStock: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var segment by remember { mutableStateOf(MarketSegment.Equity) }

    val futures = IndianMarketUniverse.futuresAndOptions.filter { it.endsWith("_FUT") }
    val options = IndianMarketUniverse.futuresAndOptions.filter { it.endsWith("_CE") || it.endsWith("_PE") }

    val activeList = when (segment) {
        MarketSegment.Equity -> symbols
        MarketSegment.Futures -> futures
        MarketSegment.Options -> options
    }

    val filtered = activeList.filter { symbol ->
        symbol.contains(query, ignoreCase = true) ||
            (quotes[symbol]?.companyName?.contains(query, true) == true) ||
            (IndianMarketUniverse.companies[symbol]?.contains(query, true) == true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it.uppercase() },
            label = { Text("Search NSE symbol (example: RELIANCE)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MarketSegment.entries.forEach { item ->
                FilterChip(
                    selected = segment == item,
                    onClick = { segment = item },
                    label = { Text(item.label) }
                )
            }
        }
        Spacer(Modifier.height(10.dp))

        if (filtered.isEmpty() && query.isNotBlank()) {
            Card(
                onClick = { onOpenStock(query) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Open custom symbol", fontWeight = FontWeight.SemiBold)
                        Text(query, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("View Chart")
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered) { symbol ->
                if (segment == MarketSegment.Equity) {
                    val quote = quotes[symbol]
                    if (quote == null) {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(symbol, fontWeight = FontWeight.SemiBold)
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            }
                        }
                    } else {
                        StockRowCard(
                            quote = quote,
                            onClick = { onOpenStock(symbol) }
                        )
                    }
                } else {
                    DerivativeRowCard(
                        symbol = symbol,
                        onClick = { onOpenStock(symbol) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DerivativeRowCard(
    symbol: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(symbol, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Derivative instrument | Chart available", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StockRowCard(
    quote: MarketQuote,
    onClick: () -> Unit
) {
    val isUp = quote.change >= 0
    val changeColor = if (isUp) ProfitGreen else LossRed
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(quote.symbol, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(quote.companyName, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, maxLines = 1)
                Spacer(Modifier.height(2.dp))
                Text("INR ${"%.2f".format(quote.currentPrice)}", fontWeight = FontWeight.SemiBold)
                Text(
                    "${if (isUp) "+" else ""}${"%.2f".format(quote.change)} (${if (isUp) "+" else ""}${"%.2f".format(quote.changePercent)}%)",
                    color = changeColor,
                    fontSize = 12.sp
                )
            }
            SparklineChart(
                points = quote.sparkline,
                lineColor = changeColor,
                modifier = Modifier
                    .width(92.dp)
                    .height(46.dp)
            )
        }
    }
}

@Composable
private fun SparklineChart(
    points: List<Float>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas

        val maxValue = points.maxOrNull() ?: return@Canvas
        val minValue = points.minOrNull() ?: return@Canvas
        val range = max(0.001f, maxValue - minValue)
        val stepX = size.width / (points.size - 1)

        val path = Path()
        points.forEachIndexed { index, value ->
            val x = index * stepX
            val normalized = (value - minValue) / range
            val y = size.height - (normalized * size.height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path = path, color = lineColor, style = Stroke(width = 3f))
    }
}

@Composable
private fun StockDetailScreen(
    symbol: String,
    quote: MarketQuote?,
    status: String?,
    onBack: () -> Unit,
    onBuy: (Int) -> Unit,
    onSell: () -> Unit,
    onCall: (Int) -> Unit,
    onPut: (Int) -> Unit,
    onDismissStatus: () -> Unit
) {
    var quantityText by remember { mutableStateOf("1") }
    var range by remember { mutableStateOf("1D") }
    val qty = quantityText.toIntOrNull() ?: 0
    val isTradable = quote != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF06080D))
            .padding(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onBack) { Text("Back") }
            Text(symbol, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
        }
        Spacer(Modifier.height(6.dp))

        if (quote == null) {
            Text("Live quote loading for $symbol...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            val isUp = quote.change >= 0
            val changeColor = if (isUp) ProfitGreen else LossRed
            Text("INR ${"%.2f".format(quote.currentPrice)}", style = MaterialTheme.typography.headlineLarge)
            Text(
                "${if (isUp) "+" else ""}${"%.2f".format(quote.change)} (${if (isUp) "+" else ""}${"%.2f".format(quote.changePercent)}%)",
                color = changeColor
            )
            Spacer(Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatChip("High", "INR ${"%.2f".format(quote.dayHigh)}")
                StatChip("Low", "INR ${"%.2f".format(quote.dayLow)}")
                StatChip("Vol", quote.volume.toString())
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("1D", "1W", "1M", "1Y").forEach { item ->
                FilterChip(
                    selected = range == item,
                    onClick = { range = item },
                    label = { Text(item) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        TradingViewChart(symbol = symbol, range = range)

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = quantityText,
            onValueChange = { quantityText = it.filter(Char::isDigit).ifBlank { "0" } },
            label = { Text("Quantity") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            UdhaarPayButton(
                text = "BUY",
                onClick = { if (qty > 0) onBuy(qty) },
                enabled = isTradable,
                modifier = Modifier.weight(1f)
            )
            UdhaarPayButton(
                text = "SELL",
                onClick = onSell,
                enabled = isTradable,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            UdhaarPayButton(
                text = "CALL",
                onClick = { if (qty > 0) onCall(qty) },
                enabled = isTradable,
                modifier = Modifier.weight(1f)
            )
            UdhaarPayButton(
                text = "PUT",
                onClick = { if (qty > 0) onPut(qty) },
                enabled = isTradable,
                modifier = Modifier.weight(1f)
            )
        }

        if (!isTradable) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Only chart is available for this symbol currently. Trading unlocks after live quote arrives.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        if (!status.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(status.orEmpty(), color = MaterialTheme.colorScheme.primary)
            UdhaarPayTextButton(text = "Dismiss", onClick = onDismissStatus)
        }
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun TradingViewChart(symbol: String, range: String) {
    val context = LocalContext.current
    val chartSymbol = remember(symbol) { mapChartSymbol(symbol) }
    val interval = remember(range) {
        when (range) {
            "1D" -> "5"
            "1W" -> "30"
            "1M" -> "60"
            else -> "D"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                }
            },
            update = { webView ->
                val html = buildTradingViewHtml(chartSymbol, interval)
                webView.loadDataWithBaseURL("https://www.tradingview.com", html, "text/html", "utf-8", null)
            }
        )
    }
}

private fun mapChartSymbol(symbol: String): String {
    return when {
        symbol.contains("_") -> {
            val root = symbol.substringBefore("_")
            when (root.uppercase()) {
                "NIFTY", "BANKNIFTY", "FINNIFTY", "MIDCPNIFTY" -> "NSE:$root"
                else -> "NSE:$root"
            }
        }

        else -> "NSE:${symbol.uppercase()}"
    }
}

private fun buildTradingViewHtml(chartSymbol: String, interval: String): String {
    return """
        <html>
          <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
            <style>body,html{margin:0;padding:0;background:#06080D;}#tv{width:100%;height:360px;}</style>
          </head>
          <body>
            <div id="tv"></div>
            <script src="https://s3.tradingview.com/tv.js"></script>
            <script type="text/javascript">
              new TradingView.widget({
                "autosize": true,
                "symbol": "$chartSymbol",
                "interval": "$interval",
                "timezone": "Asia/Kolkata",
                "theme": "dark",
                "style": "1",
                "locale": "en",
                "hide_top_toolbar": true,
                "allow_symbol_change": false,
                "container_id": "tv"
              });
            </script>
          </body>
        </html>
    """.trimIndent()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PortfolioScreen(
    summary: PortfolioSummary,
    holdings: List<HoldingItem>,
    openTrades: List<Trade>,
    closedTrades: List<Trade>,
    onCloseTrade: (Trade) -> Unit,
    onResetAccount: () -> Unit
) {
    var confirmReset by remember { mutableStateOf(false) }
    val pageTitles = listOf("Holdings", "Open", "Closed", "P&L")
    val pagerState = rememberPagerState(pageCount = { pageTitles.size })

    val pnlGlow by animateColorAsState(
        targetValue = if (summary.totalProfitLoss >= 0) Color(0x1F2FE27D) else Color(0x26FF5D6C),
        animationSpec = tween(400),
        label = "pnl_glow"
    )
    val animatedTotalPnl by animateFloatAsState(summary.totalProfitLoss.toFloat(), tween(400), label = "total_pnl_anim")
    val animatedTodayPnl by animateFloatAsState(summary.todayProfitLoss.toFloat(), tween(400), label = "today_pnl_anim")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF070A10), pnlGlow, Color(0xFF070A10))
                )
            )
            .padding(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text("Portfolio Value", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("INR ${"%,.2f".format(summary.totalPortfolioValue)}", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Total P&L: INR ${"%,.2f".format(animatedTotalPnl)}",
                    color = if (animatedTotalPnl >= 0f) ProfitGreen else LossRed
                )
                Text(
                    "Today's P&L: INR ${"%,.2f".format(animatedTodayPnl)}",
                    color = if (animatedTodayPnl >= 0f) ProfitGreen else LossRed,
                    fontSize = 12.sp
                )
            }
            TextButton(onClick = { confirmReset = true }) {
                Text("Reset")
            }
        }
        Spacer(Modifier.height(10.dp))
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> HoldingsPage(holdings)
                1 -> OpenTradesPage(openTrades, onCloseTrade)
                2 -> ClosedTradesPage(closedTrades)
                else -> SummaryPage(summary)
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageTitles.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 9.dp else 7.dp)
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Reset Paper Trading Account") },
            text = { Text("This will delete all paper trades and reset balance to INR 1,00,000.") },
            confirmButton = {
                UdhaarPayButton(
                    text = "Reset",
                    onClick = {
                        onResetAccount()
                        confirmReset = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun HoldingsPage(holdings: List<HoldingItem>) {
    if (holdings.isEmpty()) {
        Text("No holdings yet.")
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(holdings) { item ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text("${item.symbol} | ${item.companyName}", fontWeight = FontWeight.SemiBold)
                    Text("Qty: ${item.quantity} | Avg: INR ${"%.2f".format(item.avgEntry)}")
                    Text("Current: INR ${"%.2f".format(item.currentPrice)}")
                    Text(
                        "P&L: INR ${"%.2f".format(item.pnl)}",
                        color = if (item.pnl >= 0) ProfitGreen else LossRed
                    )
                }
            }
        }
    }
}

@Composable
private fun OpenTradesPage(
    openTrades: List<Trade>,
    onCloseTrade: (Trade) -> Unit
) {
    if (openTrades.isEmpty()) {
        Text("No open trades.")
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(openTrades) { trade ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text("${trade.stockSymbol} | ${trade.tradeType}", fontWeight = FontWeight.SemiBold)
                    Text("Qty: ${trade.quantity} | Entry: INR ${"%.2f".format(trade.entryPrice)}")
                    Text("Current: INR ${"%.2f".format(trade.currentPrice)}")
                    Text(
                        "P&L: INR ${"%.2f".format(trade.profitLoss)}",
                        color = if (trade.profitLoss >= 0) ProfitGreen else LossRed
                    )
                    TextButton(onClick = { onCloseTrade(trade) }) { Text("Close Trade") }
                }
            }
        }
    }
}

@Composable
private fun ClosedTradesPage(closedTrades: List<Trade>) {
    if (closedTrades.isEmpty()) {
        Text("No closed trades.")
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(closedTrades) { trade ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text("${trade.stockSymbol} | ${trade.tradeType}", fontWeight = FontWeight.SemiBold)
                    Text("Qty: ${trade.quantity} | Entry: INR ${"%.2f".format(trade.entryPrice)}")
                    Text("Exit: INR ${"%.2f".format(trade.currentPrice)}")
                    Text(
                        "P&L: INR ${"%.2f".format(trade.profitLoss)}",
                        color = if (trade.profitLoss >= 0) ProfitGreen else LossRed
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryPage(summary: PortfolioSummary) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text("Profit & Loss Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Total Portfolio Value: INR ${"%,.2f".format(summary.totalPortfolioValue)}")
            Text("Total Invested: INR ${"%,.2f".format(summary.totalInvested)}")
            Text(
                "Total P&L: INR ${"%,.2f".format(summary.totalProfitLoss)}",
                color = if (summary.totalProfitLoss >= 0) ProfitGreen else LossRed
            )
            Text(
                "Today's P&L: INR ${"%,.2f".format(summary.todayProfitLoss)}",
                color = if (summary.todayProfitLoss >= 0) ProfitGreen else LossRed
            )
        }
    }
}
