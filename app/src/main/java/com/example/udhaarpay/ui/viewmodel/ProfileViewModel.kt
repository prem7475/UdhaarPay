package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.model.User
import com.example.udhaarpay.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                _currentUser.value = user
                if (user != null) {
                    _fullName.value = user.name
                    _email.value = user.email ?: ""
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading user profile")
                _error.value = "Failed to load profile"
            }
        }
    }

    fun setEditing(isEdit: Boolean) {
        _isEditing.value = isEdit
        if (!isEdit) {
            // Reset form if cancelled
                _currentUser.value?.let {
                    _fullName.value = it.name
                    _email.value = it.email ?: ""
                }
        }
    }

    fun setFullName(name: String) {
        _fullName.value = name
    }

    fun setEmail(emailValue: String) {
        _email.value = emailValue
    }

    fun updateProfile() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val currentUser = _currentUser.value ?: return@launch
                val updatedUser = currentUser.copy(
                    name = _fullName.value,
                    email = _email.value
                )

                authRepository.saveUser(updatedUser)
                _currentUser.value = updatedUser
                _isEditing.value = false

                Timber.d("Profile updated successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error updating profile")
                _error.value = e.message ?: "Failed to update profile"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        // Clear user data and navigate to auth screen
        viewModelScope.launch {
            _currentUser.value = null
            _fullName.value = ""
            _email.value = ""
            _isEditing.value = false
            Timber.d("User logged out")
        }
    }
}
