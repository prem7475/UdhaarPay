package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.data.local.dao.UserProfileDao
import com.udhaarpay.app.data.local.entities.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao
) : ViewModel() {
    val userProfiles: StateFlow<List<UserProfile>> =
        userProfileDao.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val currentUser: StateFlow<UserProfile?> = userProfileDao.getAll()
        .map { list -> list.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    fun insert(profile: UserProfile) {
        viewModelScope.launch {
            userProfileDao.insert(profile)
            _statusMessage.value = "Profile saved"
        }
    }

    fun delete(profile: UserProfile) {
        viewModelScope.launch { userProfileDao.delete(profile) }
    }

    fun update(profile: UserProfile) {
        viewModelScope.launch {
            userProfileDao.update(profile)
            _statusMessage.value = "Profile updated"
        }
    }

    fun saveProfile(
        fullName: String,
        email: String,
        phone: String,
        profilePhotoUrl: String?,
        address: String? = null
    ) {
        viewModelScope.launch {
            val existing = currentUser.value
            val profile = (existing ?: defaultProfile()).copy(
                fullName = fullName,
                email = email,
                phone = phone,
                profilePhotoUrl = profilePhotoUrl,
                address = address ?: (existing?.address ?: "Mumbai")
            )
            userProfileDao.insert(profile)
            _statusMessage.value = "Profile updated successfully"
        }
    }

    fun linkUpiToPhone(phone: String) {
        viewModelScope.launch {
            if (phone.isBlank()) {
                _statusMessage.value = "Enter phone number to link UPI."
                return@launch
            }
            val existing = currentUser.value ?: defaultProfile()
            val cleanPhone = phone.filter { it.isDigit() }
            val generatedUpi = "${cleanPhone}@udhaarpay"
            userProfileDao.insert(
                existing.copy(
                    phone = phone,
                    upiId = generatedUpi
                )
            )
            _statusMessage.value = "UPI linked successfully: $generatedUpi"
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    private fun defaultProfile(): UserProfile {
        val now = System.currentTimeMillis()
        return UserProfile(
            userId = "user_$now",
            fullName = "",
            email = "",
            phone = "",
            dateOfBirth = 0L,
            gender = "",
            address = "",
            city = "",
            state = "",
            pincode = "",
            upiId = null,
            walletPinFreeLimit = 200.0,
            profilePhotoUrl = null,
            panNumber = "",
            aadhaarNumber = "",
            kycStatus = false,
            kycDate = null
        )
    }
}
