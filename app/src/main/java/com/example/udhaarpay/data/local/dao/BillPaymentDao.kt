package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.udhaarpay.data.model.BillPayment
import kotlinx.coroutines.flow.Flow

@Dao
interface BillPaymentDao {
    @Insert
    suspend fun insertBill(bill: BillPayment): Long

    @Query("SELECT * FROM bills WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserBills(userId: Long): Flow<List<BillPayment>>

    @Query("SELECT * FROM bills WHERE id = :billId")
    suspend fun getBillById(billId: Long): BillPayment?

    @Query("SELECT COUNT(*) FROM bills WHERE userId = :userId")
    suspend fun getBillCount(userId: Long): Int
}
