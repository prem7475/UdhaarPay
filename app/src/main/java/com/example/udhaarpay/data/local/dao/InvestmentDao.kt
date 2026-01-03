package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.udhaarpay.data.model.Investment
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {
    @Insert
    suspend fun insertInvestment(investment: Investment): Long

    @Query("SELECT * FROM investments WHERE userId = :userId ORDER BY visitDate DESC")
    fun getUserInvestments(userId: Long): Flow<List<Investment>>

    @Query("SELECT * FROM investments WHERE id = :investmentId")
    suspend fun getInvestmentById(investmentId: Long): Investment?

    @Query("SELECT COUNT(*) FROM investments WHERE userId = :userId")
    suspend fun getInvestmentCount(userId: Long): Int
}
