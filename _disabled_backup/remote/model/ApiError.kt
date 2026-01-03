package com.example.udhaarpay.data.remote.model

import com.google.gson.annotations.SerializedName

data class ApiError(
    @SerializedName("error")
    val error: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("details")
    val details: String? = null
)
