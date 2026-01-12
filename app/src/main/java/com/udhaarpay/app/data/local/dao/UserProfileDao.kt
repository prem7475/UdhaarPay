package com.udhaarpay.app.data.local.dao

import androidx.room.*
import com.udhaarpay.app.data.local.entities.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserProfile): Long

    @Update
    suspend fun update(user: UserProfile): Int

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    fun getById(userId: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles")
    fun getAll(): Flow<List<UserProfile>>

    @Delete
    suspend fun delete(user: UserProfile): Int
}
