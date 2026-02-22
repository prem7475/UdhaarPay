package com.udhaarpay.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.udhaarpay.app.data.local.entities.PaperTradingAccount
import com.udhaarpay.app.data.local.entities.Trade
import kotlinx.coroutines.flow.Flow

@Dao
interface PaperTradingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: PaperTradingAccount): Long

    @Update
    suspend fun updateAccount(account: PaperTradingAccount): Int

    @Query("SELECT * FROM paper_trading_account ORDER BY id LIMIT 1")
    fun observeAccount(): Flow<PaperTradingAccount?>

    @Query("SELECT * FROM paper_trading_account ORDER BY id LIMIT 1")
    suspend fun getAccountOnce(): PaperTradingAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: Trade): Long

    @Update
    suspend fun updateTrade(trade: Trade): Int

    @Query("SELECT * FROM trades ORDER BY timestamp DESC")
    fun observeAllTrades(): Flow<List<Trade>>

    @Query("SELECT * FROM trades WHERE tradeStatus = 'OPEN' ORDER BY timestamp DESC")
    fun observeOpenTrades(): Flow<List<Trade>>

    @Query("SELECT * FROM trades WHERE tradeStatus = 'CLOSED' ORDER BY timestamp DESC")
    fun observeClosedTrades(): Flow<List<Trade>>

    @Query("SELECT * FROM trades WHERE tradeStatus = 'OPEN'")
    suspend fun getOpenTradesSnapshot(): List<Trade>

    @Query("SELECT * FROM trades WHERE tradeStatus = 'CLOSED'")
    suspend fun getClosedTradesSnapshot(): List<Trade>

    @Query("SELECT * FROM trades WHERE tradeId = :tradeId LIMIT 1")
    suspend fun getTradeById(tradeId: Long): Trade?

    @Query(
        "SELECT * FROM trades " +
            "WHERE tradeStatus = 'OPEN' AND stockSymbol = :symbol AND tradeType = 'BUY' " +
            "ORDER BY timestamp LIMIT 1"
    )
    suspend fun getFirstOpenBuy(symbol: String): Trade?

    @Query("DELETE FROM trades")
    suspend fun deleteAllTrades(): Int
}

