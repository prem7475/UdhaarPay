package com.example.udhaarpay.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.udhaarpay.data.model.RecurringPayment
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringPaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: RecurringPayment)

    @Query("SELECT * FROM recurring_payments WHERE id = :id")
    suspend fun getById(id: String): RecurringPayment?

    @Query("SELECT * FROM recurring_payments")
    fun getAll(): Flow<List<RecurringPayment>>
}
