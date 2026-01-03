package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.udhaarpay.data.model.Insurance
import kotlinx.coroutines.flow.Flow

@Dao
interface InsuranceDao {
    @Insert
    suspend fun insertInsurance(insurance: Insurance): Long

    @Query("SELECT * FROM insurance WHERE userId = :userId ORDER BY visitDate DESC")
    fun getUserInsurance(userId: Long): Flow<List<Insurance>>

    @Query("SELECT * FROM insurance WHERE id = :insuranceId")
    suspend fun getInsuranceById(insuranceId: Long): Insurance?

    @Query("SELECT COUNT(*) FROM insurance WHERE userId = :userId")
    suspend fun getInsuranceCount(userId: Long): Int
}
