package com.udhaarpay.app.repository

import com.udhaarpay.app.data.local.dao.PaperTradingDao
import com.udhaarpay.app.data.local.entities.PaperTradingAccount
import com.udhaarpay.app.data.local.entities.Trade
import com.udhaarpay.app.data.model.MarketQuote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaperTradingRepository @Inject constructor(
    private val dao: PaperTradingDao
) {
    private val mutex = Mutex()

    fun observeAccount(): Flow<PaperTradingAccount?> = dao.observeAccount()
    fun observeOpenTrades(): Flow<List<Trade>> = dao.observeOpenTrades()
    fun observeClosedTrades(): Flow<List<Trade>> = dao.observeClosedTrades()
    fun observeAllTrades(): Flow<List<Trade>> = dao.observeAllTrades()

    suspend fun ensureAccountExists() {
        mutex.withLock {
            if (dao.getAccountOnce() == null) {
                dao.insertAccount(
                    PaperTradingAccount(
                        virtualBalance = STARTING_BALANCE,
                        totalInvested = 0.0,
                        totalProfitLoss = 0.0,
                        createdDate = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    suspend fun placeOpenTrade(
        symbol: String,
        companyName: String,
        type: String,
        quantity: Int,
        price: Double
    ): Result<Unit> = mutex.withLock {
        if (quantity <= 0) return@withLock Result.failure(IllegalArgumentException("Quantity must be greater than 0"))
        val account = dao.getAccountOnce() ?: return@withLock Result.failure(IllegalStateException("Account not found"))
        val requiredCapital = quantity * price
        if (account.virtualBalance < requiredCapital) {
            return@withLock Result.failure(IllegalStateException("Insufficient virtual balance"))
        }

        dao.insertTrade(
            Trade(
                stockSymbol = symbol,
                companyName = companyName,
                tradeType = type,
                quantity = quantity,
                entryPrice = price,
                currentPrice = price,
                profitLoss = 0.0,
                tradeStatus = STATUS_OPEN,
                timestamp = System.currentTimeMillis()
            )
        )

        dao.updateAccount(
            account.copy(
                virtualBalance = account.virtualBalance - requiredCapital
            )
        )
        recalculateAccountLocked()
        Result.success(Unit)
    }

    suspend fun closeOpenBuyBySell(symbol: String, currentPrice: Double): Result<Trade> = mutex.withLock {
        val account = dao.getAccountOnce() ?: return@withLock Result.failure(IllegalStateException("Account not found"))
        val openBuy = dao.getFirstOpenBuy(symbol) ?: return@withLock Result.failure(
            IllegalStateException("No open BUY position for $symbol")
        )

        val pnl = calculatePnl(
            tradeType = TYPE_BUY,
            entry = openBuy.entryPrice,
            current = currentPrice,
            quantity = openBuy.quantity
        )
        val credited = currentPrice * openBuy.quantity
        val closedTrade = openBuy.copy(
            tradeType = TYPE_SELL,
            currentPrice = currentPrice,
            profitLoss = pnl,
            tradeStatus = STATUS_CLOSED,
            timestamp = System.currentTimeMillis()
        )

        dao.updateTrade(closedTrade)
        dao.updateAccount(account.copy(virtualBalance = account.virtualBalance + credited))
        recalculateAccountLocked()

        Result.success(closedTrade)
    }

    suspend fun closeTrade(tradeId: Long, currentPrice: Double): Result<Trade> = mutex.withLock {
        val account = dao.getAccountOnce() ?: return@withLock Result.failure(IllegalStateException("Account not found"))
        val trade = dao.getTradeById(tradeId) ?: return@withLock Result.failure(IllegalStateException("Trade not found"))
        if (trade.tradeStatus != STATUS_OPEN) {
            return@withLock Result.failure(IllegalStateException("Trade already closed"))
        }

        val pnl = calculatePnl(
            tradeType = trade.tradeType,
            entry = trade.entryPrice,
            current = currentPrice,
            quantity = trade.quantity
        )
        val credited = currentPrice * trade.quantity
        val closed = trade.copy(
            tradeStatus = STATUS_CLOSED,
            currentPrice = currentPrice,
            profitLoss = pnl,
            timestamp = System.currentTimeMillis()
        )

        dao.updateTrade(closed)
        dao.updateAccount(account.copy(virtualBalance = account.virtualBalance + credited))
        recalculateAccountLocked()

        Result.success(closed)
    }

    suspend fun syncOpenTradesWithQuotes(quotes: Map<String, MarketQuote>) {
        mutex.withLock {
            val openTrades = dao.getOpenTradesSnapshot()
            if (openTrades.isEmpty()) {
                recalculateAccountLocked()
                return@withLock
            }

            openTrades.forEach { trade ->
                val quote = quotes[trade.stockSymbol] ?: return@forEach
                val pnl = calculatePnl(
                    tradeType = trade.tradeType,
                    entry = trade.entryPrice,
                    current = quote.currentPrice,
                    quantity = trade.quantity
                )
                dao.updateTrade(
                    trade.copy(
                        currentPrice = quote.currentPrice,
                        profitLoss = pnl
                    )
                )
            }

            recalculateAccountLocked()
        }
    }

    suspend fun resetAccount(): Result<Unit> = mutex.withLock {
        val account = dao.getAccountOnce() ?: return@withLock Result.failure(IllegalStateException("Account not found"))
        dao.deleteAllTrades()
        dao.updateAccount(
            account.copy(
                virtualBalance = STARTING_BALANCE,
                totalInvested = 0.0,
                totalProfitLoss = 0.0
            )
        )
        Result.success(Unit)
    }

    private suspend fun recalculateAccountLocked() {
        val account = dao.getAccountOnce() ?: return
        val openTrades = dao.getOpenTradesSnapshot()
        val closedTrades = dao.getClosedTradesSnapshot()
        val totalInvested = openTrades.sumOf { it.entryPrice * it.quantity }
        val totalPnl = openTrades.sumOf { it.profitLoss } + closedTrades.sumOf { it.profitLoss }
        dao.updateAccount(
            account.copy(
                totalInvested = totalInvested,
                totalProfitLoss = totalPnl
            )
        )
    }

    private fun calculatePnl(
        tradeType: String,
        entry: Double,
        current: Double,
        quantity: Int
    ): Double {
        val delta = when (tradeType.uppercase()) {
            TYPE_BUY -> current - entry
            TYPE_CALL -> current - entry
            TYPE_PUT -> entry - current
            TYPE_SELL -> current - entry
            else -> current - entry
        }
        return delta * quantity
    }

    companion object {
        const val STARTING_BALANCE = 100000.0
        const val TYPE_BUY = "BUY"
        const val TYPE_SELL = "SELL"
        const val TYPE_CALL = "CALL"
        const val TYPE_PUT = "PUT"
        const val STATUS_OPEN = "OPEN"
        const val STATUS_CLOSED = "CLOSED"
    }
}

