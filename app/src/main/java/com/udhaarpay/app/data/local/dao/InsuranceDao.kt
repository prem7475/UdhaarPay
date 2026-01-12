package com.udhaarpay.app.data.local.dao

import androidx.room.*
import com.udhaarpay.app.data.local.entities.Insurance
import kotlinx.coroutines.flow.Flow

@Dao
interface InsuranceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(insurance: Insurance): Long

    @Update
    suspend fun update(insurance: Insurance): Int

    @Delete
    suspend fun delete(insurance: Insurance): Int

    @Query("SELECT * FROM insurances")
    fun getAll(): Flow<List<Insurance>>

    @Query("SELECT * FROM insurances WHERE policyType = :type")
    fun getByType(type: String): Flow<List<Insurance>>

    @Query("SELECT * FROM insurances WHERE status = :status")
    fun getByStatus(status: String): Flow<List<Insurance>>

    @Query("SELECT * FROM insurances WHERE provider = :provider")
    fun getByProvider(provider: String): Flow<List<Insurance>>
}
