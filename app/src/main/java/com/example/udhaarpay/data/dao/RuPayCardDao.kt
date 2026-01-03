package com.example.udhaarpay.data.dao

import androidx.room.*
import com.example.udhaarpay.data.model.RuPayCard
import kotlinx.coroutines.flow.Flow

@Dao
interface RuPayCardDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: RuPayCard)
    
    @Update
    suspend fun updateCard(card: RuPayCard)
    
    @Delete
    suspend fun deleteCard(card: RuPayCard)
    
    @Query("SELECT * FROM rupay_cards WHERE userId = :userId ORDER BY isDefault DESC, addedDate DESC")
    fun getUserCards(userId: Int): Flow<List<RuPayCard>>
    
    @Query("SELECT * FROM rupay_cards WHERE id = :cardId")
    suspend fun getCardById(cardId: Int): RuPayCard?
    
    @Query("SELECT * FROM rupay_cards WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    fun getDefaultCard(userId: Int): Flow<RuPayCard?>
    
    @Query("UPDATE rupay_cards SET isDefault = 0 WHERE userId = :userId")
    suspend fun clearDefaultCard(userId: Int)
    
    @Query("UPDATE rupay_cards SET isDefault = 1 WHERE id = :cardId")
    suspend fun setDefaultCard(cardId: Int)
    
    @Query("SELECT COUNT(*) FROM rupay_cards WHERE userId = :userId AND isActive = 1")
    fun getActiveCardCount(userId: Int): Flow<Int>
    
    @Query("SELECT SUM(availableBalance) FROM rupay_cards WHERE userId = :userId AND isActive = 1")
    fun getTotalBalance(userId: Int): Flow<Double>
    
    @Query("UPDATE rupay_cards SET availableBalance = :balance WHERE id = :cardId")
    suspend fun updateCardBalance(cardId: Int, balance: Double)
    
    @Query("DELETE FROM rupay_cards WHERE userId = :userId")
    suspend fun deleteAllUserCards(userId: Int)
}
