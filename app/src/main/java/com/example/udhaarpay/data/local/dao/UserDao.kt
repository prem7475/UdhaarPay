package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.udhaarpay.data.local.entity.WalletEntity
import com.example.udhaarpay.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // Matches the tableName = "user_profile" in User.kt
    @Query("SELECT * FROM user_profile WHERE userId = :userId")
    fun getUser(userId: String): Flow<User?>

    @Query("SELECT * FROM user_profile WHERE userId = :userId")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM user_profile WHERE userId = :userId")
    suspend fun getUserProfile(userId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE user_profile SET walletBalance = :newBalance WHERE userId = :userId")
    suspend fun updateWalletBalance(userId: String, newBalance: Double)

    // Wallet related methods
    @Query("SELECT * FROM wallet WHERE id = 1")
    fun getWallet(): Flow<WalletEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWallet(wallet: WalletEntity)
}