package com.example.udhaarpay.data.repository

import com.example.udhaarpay.data.model.Contact
import com.example.udhaarpay.data.model.Transaction
import com.example.udhaarpay.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    // Mock implementation - in real app, this would interact with database/API
    private val users = mutableMapOf<String, User>()
    private val favoriteContacts = mutableMapOf<String, MutableList<Contact>>()

    fun getUserById(id: String): Flow<User?> {
        // Mock user data
        val user = users[id] ?: User(
            id = id,
            name = "Test User",
            email = "test@example.com",
            phoneNumber = "1234567890",
            upiId = "test@paytm",
            walletBalance = 1000.0
        )
        return flowOf(user)
    }

    suspend fun updateWalletBalance(userId: String, newBalance: Double) {
        val user = users[userId]
        if (user != null) {
            users[userId] = user.copy(walletBalance = newBalance)
        } else {
            // Create user if not exists
            users[userId] = User(
                id = userId,
                name = "Test User",
                email = "test@example.com",
                phoneNumber = "1234567890",
                upiId = "test@paytm",
                walletBalance = newBalance
            )
        }
    }

    suspend fun updateUserWalletBalance(userId: String, newBalance: Double) {
        updateWalletBalance(userId, newBalance)
    }

    suspend fun getRecentTransactions(userId: String, limit: Int): List<Transaction> {
        // Mock implementation - return empty list
        return emptyList()
    }

    suspend fun addContactToFavorites(userId: String, contact: Contact) {
        val contacts = favoriteContacts.getOrPut(userId) { mutableListOf() }
        if (!contacts.contains(contact)) {
            contacts.add(contact)
        }
    }

    fun getFavoriteContacts(userId: String): Flow<List<Contact>> {
        val contacts = favoriteContacts[userId] ?: emptyList()
        return flowOf(contacts)
    }
}
