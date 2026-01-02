package com.example.udhaarpay.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.udhaarpay.data.model.*
import kotlinx.coroutines.flow.Flow

// User DAO
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber")
    suspend fun getUserByPhone(phoneNumber: String): User?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): User?

    @Delete
    suspend fun deleteUser(user: User)
}

// Card DAO
@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: BankCard): Long

    @Update
    suspend fun updateCard(card: BankCard)

    @Query("SELECT * FROM cards WHERE userId = :userId ORDER BY isPrimary DESC, id DESC")
    fun getCardsByUserFlow(userId: Int): Flow<List<BankCard>>

    @Query("SELECT * FROM cards WHERE userId = :userId ORDER BY isPrimary DESC, id DESC")
    suspend fun getCardsByUser(userId: Int): List<BankCard>

    @Query("SELECT * FROM cards WHERE id = :cardId")
    suspend fun getCardById(cardId: Int): BankCard?

    @Query("SELECT * FROM cards WHERE userId = :userId AND isPrimary = 1 LIMIT 1")
    suspend fun getPrimaryCard(userId: Int): BankCard?

    @Query("UPDATE cards SET isPrimary = 0 WHERE userId = :userId")
    suspend fun removePrimaryFromAll(userId: Int)

    @Delete
    suspend fun deleteCard(card: BankCard)
}

// Bank Account DAO
@Dao
interface BankAccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccount(account: BankAccount): Long

    @Update
    suspend fun updateBankAccount(account: BankAccount)

    @Query("SELECT * FROM bank_accounts WHERE userId = :userId ORDER BY id DESC")
    fun getBankAccountsFlow(userId: Int): Flow<List<BankAccount>>

    @Query("SELECT * FROM bank_accounts WHERE userId = :userId ORDER BY id DESC")
    suspend fun getBankAccounts(userId: Int): List<BankAccount>

    @Query("SELECT * FROM bank_accounts WHERE id = :accountId")
    suspend fun getBankAccountById(accountId: Int): BankAccount?

    @Delete
    suspend fun deleteBankAccount(account: BankAccount)
}

// Transaction DAO
@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsFlow(userId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getTransactions(userId: Int): List<Transaction>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND category = :category ORDER BY timestamp DESC")
    suspend fun getTransactionsByCategory(userId: Int, category: String): List<Transaction>

    @Query("SELECT * FROM transactions WHERE transactionId = :txnId")
    suspend fun getTransactionById(txnId: String): Transaction?

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}

// Offer DAO
@Dao
interface OfferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: Offer): Long

    @Query("SELECT * FROM offers WHERE category = :category ORDER BY validTill DESC")
    fun getOffersByCategory(category: String): Flow<List<Offer>>

    @Query("SELECT * FROM offers ORDER BY validTill DESC")
    fun getAllOffers(): Flow<List<Offer>>

    @Query("SELECT DISTINCT category FROM offers ORDER BY category")
    suspend fun getOfferCategories(): List<String>

    @Delete
    suspend fun deleteOffer(offer: Offer)
}

// Service DAO
@Dao
interface ServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: Service): Long

    @Query("SELECT * FROM services WHERE category = :category ORDER BY serviceName")
    fun getServicesByCategory(category: String): Flow<List<Service>>

    @Query("SELECT * FROM services WHERE isActive = 1 ORDER BY category, serviceName")
    fun getAllActiveServices(): Flow<List<Service>>

    @Query("SELECT DISTINCT category FROM services ORDER BY category")
    suspend fun getServiceCategories(): List<String>

    @Delete
    suspend fun deleteService(service: Service)
}

// Transaction Category DAO
@Dao
interface TransactionCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: TransactionCategory): Long

    @Update
    suspend fun updateCategory(category: TransactionCategory)

    @Query("SELECT * FROM transaction_categories WHERE userId = :userId ORDER BY amount DESC")
    fun getCategoriesFlow(userId: Int): Flow<List<TransactionCategory>>

    @Query("SELECT * FROM transaction_categories WHERE userId = :userId ORDER BY amount DESC")
    suspend fun getCategories(userId: Int): List<TransactionCategory>

    @Delete
    suspend fun deleteCategory(category: TransactionCategory)
}

// Spending Analytics DAO
@Dao
interface SpendingAnalyticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalytics(analytics: SpendingAnalytics): Long

    @Update
    suspend fun updateAnalytics(analytics: SpendingAnalytics)

    @Query("SELECT * FROM spending_analytics WHERE userId = :userId AND month = :month")
    suspend fun getAnalyticsForMonth(userId: Int, month: String): SpendingAnalytics?

    @Query("SELECT * FROM spending_analytics WHERE userId = :userId ORDER BY month DESC LIMIT 12")
    fun getLast12MonthsAnalytics(userId: Int): Flow<List<SpendingAnalytics>>

    @Delete
    suspend fun deleteAnalytics(analytics: SpendingAnalytics)
}

// QR Scan DAO
@Dao
interface QRScanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQRScan(scan: QRScan): Long

    @Update
    suspend fun updateQRScan(scan: QRScan)

    @Query("SELECT * FROM qr_scans WHERE userId = :userId ORDER BY timestamp DESC")
    fun getQRScansFlow(userId: Int): Flow<List<QRScan>>

    @Delete
    suspend fun deleteQRScan(scan: QRScan)
}

// Notification DAO
@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification): Long

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsFlow(userId: Int): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY timestamp DESC")
    fun getUnreadNotificationsFlow(userId: Int): Flow<List<Notification>>

    @Update
    suspend fun updateNotification(notification: Notification)

    @Delete
    suspend fun deleteNotification(notification: Notification)
}
