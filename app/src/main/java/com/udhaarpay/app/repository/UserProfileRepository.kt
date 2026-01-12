package com.udhaarpay.app.repository

import com.udhaarpay.app.data.local.dao.UserProfileDao
import com.udhaarpay.app.data.local.entities.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) {
    fun getAll(): Flow<List<UserProfile>> = userProfileDao.getAll()
    suspend fun insert(profile: UserProfile): Long = userProfileDao.insert(profile)
    suspend fun delete(profile: UserProfile): Int = userProfileDao.delete(profile)
    suspend fun update(profile: UserProfile): Int = userProfileDao.update(profile)
}
