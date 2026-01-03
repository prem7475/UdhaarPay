package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.udhaarpay.data.model.MobileRecharge
import kotlinx.coroutines.flow.Flow

@Dao
interface MobileRechargeDao {
    @Insert
    suspend fun insertRecharge(recharge: MobileRecharge): Long

    @Query("SELECT * FROM recharges WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserRecharges(userId: Long): Flow<List<MobileRecharge>>

    @Query("SELECT * FROM recharges WHERE id = :rechargeId")
    suspend fun getRechargeById(rechargeId: Long): MobileRecharge?

    @Query("SELECT COUNT(*) FROM recharges WHERE userId = :userId")
    suspend fun getRechargeCount(userId: Long): Int
}
