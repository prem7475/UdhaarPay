package com.udhaarpay.app.data.local.dao

import androidx.room.*
import com.udhaarpay.app.data.local.entities.CreditCard
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: CreditCard): Long

    @Update
    suspend fun update(card: CreditCard): Int

    @Delete
    suspend fun delete(card: CreditCard): Int

    @Query("SELECT * FROM credit_cards")
    fun getAll(): Flow<List<CreditCard>>

    @Query("SELECT * FROM credit_cards WHERE cardId = :cardId LIMIT 1")
    fun getById(cardId: Long): Flow<CreditCard?>

    @Query("SELECT * FROM credit_cards WHERE cardType = :cardType")
    fun getByCardType(cardType: String): Flow<List<CreditCard>>
}
