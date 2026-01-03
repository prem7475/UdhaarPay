package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.UserDao
import com.example.udhaarpay.data.local.entity.UserProfileEntity
import com.example.udhaarpay.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    // Observe user profile directly from DAO
    val userProfile: StateFlow<UserProfileEntity?> = userDao.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    fun saveProfile(name: String, phone: String, email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // uid is fixed to 1 as per requirements for single user local app
            val profile = UserProfileEntity(
                uid = 1,
                fullName = name,
                phoneNumber = phone,
                email = email,
                profileImageUri = null // Placeholder for now
            )
            userDao.insertOrUpdateProfile(profile)
            _isLoading.value = false
        }
    }

    fun loadUserProfile(userId: Int) {
        viewModelScope.launch {
            try {
                val user = userDao.getUserById(userId)
                _user.value = user
            } catch (e: Exception) {
                _error.value = "Failed to load profile: ${e.message}"
            }
        }
    }

    fun updateUserProfile(
        userId: Int,
        name: String,
        phoneNumber: String,
        email: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userDao.getUserById(userId)
                if (user != null) {
                    val updatedUser = user.copy(
                        name = name,
                        phoneNumber = phoneNumber,
                        email = email
                    )
                    userDao.updateUser(updatedUser)
                    _user.value = updatedUser
                    _success.value = "Profile updated successfully"
                    _error.value = null
                } else {
                    _error.value = "User data not found"
                }
            } catch (e: Exception) {
                _error.value = "Update failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfileImage(userId: Int, imageUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userDao.getUserById(userId)
                if (user != null) {
                    val updatedUser = user.copy(profileImageUrl = imageUrl)
                    userDao.updateUser(updatedUser)
                    _user.value = updatedUser
                    _success.value = "Profile photo updated"
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = "Photo update failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _success.value = null
    }
}
