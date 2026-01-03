package com.example.udhaarpay.data.remote

import retrofit2.http.*
import com.google.gson.JsonObject

interface PaymentApiService {

    // Authentication APIs
    @POST("auth/send-otp")
    suspend fun sendOTP(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    @POST("auth/verify-otp")
    suspend fun verifyOTP(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    @POST("auth/register")
    suspend fun register(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    // Card APIs
    @GET("cards/{userId}")
    suspend fun getCards(@Path("userId") userId: Int): ApiResponse<List<Map<String, Any>>>

    @POST("cards/add")
    suspend fun addCard(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    @GET("cards/{cardId}/balance")
    suspend fun getCardBalance(@Path("cardId") cardId: Int): ApiResponse<Map<String, Any>>

    // Bank Account APIs
    @GET("banks/{userId}")
    suspend fun getBankAccounts(@Path("userId") userId: Int): ApiResponse<List<Map<String, Any>>>

    @POST("banks/add")
    suspend fun addBankAccount(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    @GET("banks/{accountId}/balance")
    suspend fun getBankBalance(@Path("accountId") accountId: Int): ApiResponse<Map<String, Any>>

    // Transaction APIs
    @GET("transactions/{userId}")
    suspend fun getTransactions(@Path("userId") userId: Int): ApiResponse<List<Map<String, Any>>>

    @POST("transactions/process-upi")
    suspend fun processUPIPayment(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    @POST("transactions/add")
    suspend fun addTransaction(@Body request: Map<String, Any>): ApiResponse<Map<String, String>>

    // QR Code APIs
    @POST("qr/decode")
    suspend fun decodeQRCode(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    @POST("qr/validate")
    suspend fun validateUPI(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    // Offers API
    @GET("offers")
    suspend fun getOffers(): ApiResponse<List<Map<String, Any>>>

    @GET("offers/category/{category}")
    suspend fun getOffersByCategory(@Path("category") category: String): ApiResponse<List<Map<String, Any>>>

    // Services API
    @GET("services")
    suspend fun getServices(): ApiResponse<List<Map<String, Any>>>

    @GET("services/category/{category}")
    suspend fun getServicesByCategory(@Path("category") category: String): ApiResponse<List<Map<String, Any>>>

    // User APIs
    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): ApiResponse<Map<String, String>>

    @PUT("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: Int,
        @Body request: Map<String, String>
    ): ApiResponse<Map<String, String>>

    // Analytics APIs
    @GET("analytics/spending/{userId}/{month}")
    suspend fun getSpendingAnalytics(
        @Path("userId") userId: Int,
        @Path("month") month: String
    ): ApiResponse<Map<String, Any>>

    @GET("analytics/categories/{userId}")
    suspend fun getCategoryAnalytics(@Path("userId") userId: Int): ApiResponse<List<Map<String, Any>>>

    // NFC Payment APIs
    @POST("nfc/process-payment")
    suspend fun processNFCPayment(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    // Recharge APIs
    @POST("services/recharge")
    suspend fun initiateRecharge(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    // Bill Payment APIs
    @POST("services/bill-payment")
    suspend fun initiateBillPayment(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    // Booking APIs
    @POST("services/flight-booking")
    suspend fun initiateFlightBooking(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    @POST("services/train-booking")
    suspend fun initiateTrainBooking(@Body request: Map<String, String>): ApiResponse<Map<String, String>>

    @POST("services/bus-booking")
    suspend fun initiateBusBooking(@Body request: Map<String, String>): ApiResponse<Map<String, String>>
}

// Generic API Response Wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?,
    val statusCode: Int
)
