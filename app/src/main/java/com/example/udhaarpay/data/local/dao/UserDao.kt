package com.example.udhaarpay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.udhaarpay.data.local.entity.UserProfileEntity
import com.example.udhaarpay.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // User Profile Methods
    @Query("SELECT * FROM user_profile WHERE uid = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(userProfile: UserProfileEntity)

    // Wallet Methods
    @Query("SELECT * FROM wallet WHERE id = 1")
    fun getWallet(): Flow<WalletEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWallet(wallet: WalletEntity)

    @Query("UPDATE wallet SET currentBalance = :newBalance WHERE id = 1")
    suspend fun updateWalletBalance(newBalance: Double)
}
