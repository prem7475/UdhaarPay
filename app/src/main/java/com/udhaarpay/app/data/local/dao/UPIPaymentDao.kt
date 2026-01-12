package com.udhaarpay.app.data.local.dao

import androidx.room.*
import com.udhaarpay.app.data.local.entities.UPIPayment
import kotlinx.coroutines.flow.Flow

@Dao
interface UPIPaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: UPIPayment): Long

    @Delete
    suspend fun delete(payment: UPIPayment): Int

    @Query("SELECT * FROM upi_payments")
    fun getAll(): Flow<List<UPIPayment>>

    @Query("SELECT * FROM upi_payments WHERE recipientUPI = :recipientUPI")
    fun getByRecipient(recipientUPI: String): Flow<List<UPIPayment>>

    @Query("SELECT * FROM upi_payments WHERE date = :date")
    fun getByDate(date: Long): Flow<List<UPIPayment>>

    @Query("SELECT * FROM upi_payments WHERE status = :status")
    fun getByStatus(status: String): Flow<List<UPIPayment>>
}
