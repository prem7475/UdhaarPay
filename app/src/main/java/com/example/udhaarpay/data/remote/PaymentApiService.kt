package com.example.udhaarpay.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface PaymentApiService {
    // This is a placeholder endpoint. 
    // You can add real API calls (like /pay, /balance) here later.
    @GET("health-check")
    suspend fun checkHealth(): Response<String>
}