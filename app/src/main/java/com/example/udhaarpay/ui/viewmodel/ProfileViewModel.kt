package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.local.dao.UserDao
import com.example.udhaarpay.data.local.entity.UserProfileEntity
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
}
