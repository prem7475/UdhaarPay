package com.udhaarpay.app.data.local.dao

import androidx.room.*
import com.udhaarpay.app.data.local.entities.CreditCard
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCard(creditCard: CreditCard): Long

    @Query("SELECT * FROM credit_cards WHERE userId = :userId")
    fun getUserCards(userId: String): Flow<List<CreditCard>>
}
