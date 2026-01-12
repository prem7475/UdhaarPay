package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.data.local.dao.UserProfileDao
import com.udhaarpay.app.data.local.entities.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao
) : ViewModel() {
    val userProfile: StateFlow<List<UserProfile>> =
        userProfileDao.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(profile: UserProfile) {
        viewModelScope.launch { userProfileDao.insert(profile) }
    }

    fun delete(profile: UserProfile) {
        viewModelScope.launch { userProfileDao.delete(profile) }
    }

    fun update(profile: UserProfile) {
        viewModelScope.launch { userProfileDao.update(profile) }
    }
}
