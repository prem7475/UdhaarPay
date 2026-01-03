package com.example.udhaarpay.data.remote.model

import com.google.gson.annotations.SerializedName

data class PaginatedResponse<T>(
    @SerializedName("data")
    val data: List<T>?,
    @SerializedName("pagination")
    val pagination: PaginationInfo
)

data class PaginationInfo(
    @SerializedName("page")
    val page: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean
)
