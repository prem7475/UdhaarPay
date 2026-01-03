package com.example.udhaarpay.data.remote

import com.example.udhaarpay.data.model.Transaction
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @GET("transactions")
    suspend fun getTransactions(@Header("Authorization") token: String): Response<List<Transaction>>

    @POST("transactions")
    suspend fun createTransaction(@Header("Authorization") token: String, @Body transaction: Transaction): Response<Transaction>
}
