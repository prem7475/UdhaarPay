package com.example.udhaarpay.data.repository

import com.example.udhaarpay.data.database.*
import com.example.udhaarpay.data.model.*
import com.example.udhaarpay.data.remote.PaymentApiService
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: PaymentApiService,
    private val userDao: UserDao
) {
    suspend fun sendOTP(phoneNumber: String): Result<String> = try {
        val response = apiService.sendOTP(mapOf("phoneNumber" to phoneNumber))
        if (response.success) {
            Result.success(response.message)
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error sending OTP")
        Result.failure(e)
    }

    suspend fun verifyOTP(phoneNumber: String, otp: String): Result<User> = try {
        val response = apiService.verifyOTP(mapOf(
            "phoneNumber" to phoneNumber,
            "otp" to otp
        ))
        if (response.success && response.data != null) {
            val user = User(
                id = phoneNumber,
                phoneNumber = phoneNumber,
                name = response.data["name"] as? String ?: "",
                email = response.data["email"] as? String,
                upiId = response.data["upiId"] as? String ?: "",
                isKycVerified = response.data["isKycVerified"] as? Boolean ?: false
            )
            userDao.insertUser(user)
            Result.success(user)
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error verifying OTP")
        Result.failure(e)
    }

    suspend fun getCurrentUser(): User? {
        return userDao.getCurrentUser()
    }

    suspend fun saveUser(user: User) {
        userDao.insertUser(user)
    }
}

class CardRepository @Inject constructor(
    private val apiService: PaymentApiService,
    private val cardDao: CardDao
) {
    fun getCardsByUser(userId: Int): Flow<List<BankCard>> {
        return cardDao.getCardsByUserFlow(userId)
    }

    suspend fun addCard(card: BankCard): Result<Long> = try {
        val cardId = cardDao.insertCard(card)
        Result.success(cardId)
    } catch (e: Exception) {
        Timber.e(e, "Error adding card")
        Result.failure(e)
    }

    suspend fun getCardBalance(cardId: Int): Result<Double> = try {
        val response = apiService.getCardBalance(cardId)
        if (response.success && response.data != null) {
            val balance = (response.data["balance"] as? Number)?.toDouble() ?: 0.0
            Result.success(balance)
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error fetching card balance")
        Result.failure(e)
    }

    suspend fun deleteCard(card: BankCard) {
        cardDao.deleteCard(card)
    }

    suspend fun setPrimaryCard(userId: Int, cardId: Int) = try {
        cardDao.removePrimaryFromAll(userId)
        val card = cardDao.getCardById(cardId)
        if (card != null) {
            cardDao.updateCard(card.copy(isPrimary = true))
            Result.success(Unit)
        } else {
            Result.failure(Exception("Card not found"))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error setting primary card")
        Result.failure(e)
    }
}

class BankRepository @Inject constructor(
    private val apiService: PaymentApiService,
    private val bankAccountDao: BankAccountDao
) {
    fun getBankAccounts(userId: Int): Flow<List<BankAccount>> {
        return bankAccountDao.getBankAccountsFlow(userId)
    }

    suspend fun addBankAccount(account: BankAccount): Result<Long> = try {
        val accountId = bankAccountDao.insertBankAccount(account)
        Result.success(accountId)
    } catch (e: Exception) {
        Timber.e(e, "Error adding bank account")
        Result.failure(e)
    }

    suspend fun getBankBalance(accountId: Int): Result<Double> = try {
        val response = apiService.getBankBalance(accountId)
        if (response.success && response.data != null) {
            val balance = (response.data["balance"] as? Number)?.toDouble() ?: 0.0
            Result.success(balance)
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error fetching bank balance")
        Result.failure(e)
    }

    suspend fun deleteBankAccount(account: BankAccount) {
        bankAccountDao.deleteBankAccount(account)
    }
}
// NOTE: TransactionRepository is defined in TransactionRepository.kt (avoid duplicate)


class OfferRepository @Inject constructor(
    private val apiService: PaymentApiService,
    private val offerDao: OfferDao
) {
    fun getOffersByCategory(category: String): Flow<List<Offer>> {
        return offerDao.getOffersByCategory(category)
    }

    fun getAllOffers(): Flow<List<Offer>> {
        return offerDao.getAllOffers()
    }

    suspend fun getOfferCategories(): Result<List<String>> = try {
        val categories = offerDao.getOfferCategories()
        Result.success(categories)
    } catch (e: Exception) {
        Timber.e(e, "Error fetching offer categories")
        Result.failure(e)
    }

    suspend fun refreshOffers() = try {
        val response = apiService.getOffers()
        if (response.success && response.data != null) {
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error refreshing offers")
        Result.failure(e)
    }
}

class ServiceRepository @Inject constructor(
    private val apiService: PaymentApiService,
    private val serviceDao: ServiceDao
) {
    fun getServicesByCategory(category: String): Flow<List<Service>> {
        return serviceDao.getServicesByCategory(category)
    }

    fun getAllActiveServices(): Flow<List<Service>> {
        return serviceDao.getAllActiveServices()
    }

    suspend fun getServiceCategories(): Result<List<String>> = try {
        val categories = serviceDao.getServiceCategories()
        Result.success(categories)
    } catch (e: Exception) {
        Timber.e(e, "Error fetching service categories")
        Result.failure(e)
    }

    suspend fun refreshServices() = try {
        val response = apiService.getServices()
        if (response.success) {
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error refreshing services")
        Result.failure(e)
    }
}

class AnalyticsRepository @Inject constructor(
    private val apiService: PaymentApiService,
    private val analyticsDao: SpendingAnalyticsDao,
    private val categoryDao: TransactionCategoryDao
) {
    suspend fun getMonthlyAnalytics(userId: Int, month: String): Flow<SpendingAnalytics?> {
        return kotlinx.coroutines.flow.flow {
            emit(analyticsDao.getAnalyticsForMonth(userId, month))
        }
    }

    fun getLast12MonthsAnalytics(userId: Int): Flow<List<SpendingAnalytics>> {
        return analyticsDao.getLast12MonthsAnalytics(userId)
    }

    fun getCategoryStats(userId: Int): Flow<List<TransactionCategory>> {
        return categoryDao.getCategoriesFlow(userId)
    }
}

class QRRepository @Inject constructor(
    private val apiService: PaymentApiService,
    private val qrScanDao: QRScanDao
) {
    suspend fun decodeQRCode(qrData: String): Result<Map<String, String>> = try {
        val response = apiService.decodeQRCode(mapOf("qrData" to qrData))
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error decoding QR code")
        Result.failure(e)
    }

    suspend fun validateUPI(upiId: String): Result<Map<String, String>> = try {
        val response = apiService.validateUPI(mapOf("upiId" to upiId))
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error validating UPI")
        Result.failure(e)
    }
}
