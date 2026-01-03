package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.udhaarpay.data.model.Udhari
import kotlinx.coroutines.flow.Flow

@Dao
interface UdhariDao {
    // Get all Udhari records
    @Query("SELECT * FROM udhari_records ORDER BY createdAt DESC")
    fun getAllUdhariRecords(): Flow<List<Udhari>>

    @Query("SELECT * FROM udhari_records WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllUdhari(userId: String): Flow<List<Udhari>>

    @Query("SELECT * FROM udhari_records WHERE id = :id")
    suspend fun getUdhariById(id: Long): Udhari?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUdhari(udhari: Udhari)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUdhariRecord(record: Udhari)

    @Update
    suspend fun updateUdhari(udhari: Udhari)

    @Delete
    suspend fun deleteUdhari(udhari: Udhari)

    @Query("UPDATE udhari_records SET isPaid = 1 WHERE id = :id")
    suspend fun settleUdhari(id: Long)

    @Query("SELECT SUM(amount) FROM udhari_records WHERE userId = :userId AND type = 'GIVEN' AND isPaid = 0")
    fun getTotalGiven(userId: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM udhari_records WHERE userId = :userId AND type = 'TAKEN' AND isPaid = 0")
    fun getTotalTaken(userId: String): Flow<Double?>
}