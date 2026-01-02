package com.example.udhaarpay.data.remote.model

/**
 * Generic API response wrapper
 */
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val error: ApiError) : ApiResponse<Nothing>()
    data object Loading : ApiResponse<Nothing>()
}
