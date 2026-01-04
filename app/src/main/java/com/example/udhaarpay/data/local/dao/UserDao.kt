package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.udhaarpay.data.model.User // <--- CRITICAL IMPORT
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUser(userId: String): Flow<User?>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserOneShot(userId: String): User?

    @Query("DELETE FROM users")
    suspend fun clearAll()
}