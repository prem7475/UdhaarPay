package com.udhaarpay.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.udhaarpay.app.data.model.CreditCard
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CreditCard): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditCard(card: CreditCard): Long
    @Update
    suspend fun updateCard(card: CreditCard)
    @Delete
    suspend fun deleteCard(card: CreditCard)
    @Query("SELECT * FROM credit_cards")
    fun getAllCreditCards(): Flow<List<CreditCard>>
    @Query("SELECT * FROM credit_cards WHERE id = :id")
    suspend fun getCardById(id: Long): CreditCard?
    @Query("SELECT * FROM credit_cards WHERE userId = :userId AND isActive = 1 ORDER BY isDefault DESC, createdAt DESC")
    fun getUserCards(userId: String): Flow<List<CreditCard>>
    @Query("SELECT * FROM credit_cards WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    suspend fun getDefaultCard(userId: String): CreditCard?
    @Query("UPDATE credit_cards SET isDefault = 0 WHERE userId = :userId")
    suspend fun clearDefaultFlag(userId: String)
    @Query("UPDATE credit_cards SET isDefault = 1 WHERE id = :cardId")
    suspend fun setDefaultCard(cardId: Long)
    @Query("DELETE FROM credit_cards WHERE userId = :userId")
    suspend fun deleteUserCards(userId: String)
    @Query("SELECT COUNT(*) FROM credit_cards WHERE userId = :userId AND isActive = 1")
    fun getActiveCardCount(userId: String): Flow<Int>
}
