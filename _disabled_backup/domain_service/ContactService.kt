package com.example.udhaarpay.domain.service

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import com.example.udhaarpay.data.model.Contact
import com.example.udhaarpay.data.repository.UserRepository
import com.example.udhaarpay.utils.ErrorHandler
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactService @Inject constructor(
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) {

    // Get all contacts with UPI apps
    suspend fun getContactsWithUpiApps(contentResolver: ContentResolver): List<Contact> {
        return try {
            val contacts = mutableListOf<Contact>()

            val projection = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
            )

            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                    val hasPhone = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                    if (hasPhone == "1") {
                        val phoneNumbers = getPhoneNumbers(contentResolver, id)
                        phoneNumbers.forEach { phone ->
                            contacts.add(Contact(
                                id = id,
                                name = name,
                                phoneNumber = phone,
                                upiId = generateUpiId(phone),
                                hasUpiApp = checkIfHasUpiApp(phone)
                            ))
                        }
                    }
                }
            }

            contacts

        } catch (e: Exception) {
            errorHandler.handleError(e, "ContactService.getContactsWithUpiApps")
            emptyList()
        }
    }

    // Search contacts by name or phone
    suspend fun searchContacts(
        contentResolver: ContentResolver,
        query: String
    ): List<Contact> {
        return try {
            val allContacts = getContactsWithUpiApps(contentResolver)
            allContacts.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.phoneNumber.contains(query) ||
                it.upiId.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            errorHandler.handleError(e, "ContactService.searchContacts")
            emptyList()
        }
    }

    // Get frequent payment contacts
    suspend fun getFrequentContacts(userId: String, limit: Int = 10): List<Contact> {
        return try {
            // Get recent transactions and extract unique contacts
            val recentTransactions = userRepository.getRecentTransactions(userId, limit * 2)

            val contactMap = mutableMapOf<String, Contact>()

            recentTransactions.forEach { transaction ->
                val upiId = transaction.senderUpiId ?: transaction.receiverUpiId
                val name = transaction.senderName ?: transaction.receiverName

                if (upiId != null && name != null && !contactMap.containsKey(upiId)) {
                    contactMap[upiId] = Contact(
                        id = upiId,
                        name = name,
                        phoneNumber = "",
                        upiId = upiId,
                        hasUpiApp = true,
                        isFrequent = true
                    )
                }
            }

            contactMap.values.take(limit).toList()

        } catch (e: Exception) {
            errorHandler.handleError(e, "ContactService.getFrequentContacts")
            emptyList()
        }
    }

    // Add contact to favorites
    suspend fun addToFavorites(userId: String, contact: Contact) {
        try {
            userRepository.addContactToFavorites(userId, contact)
        } catch (e: Exception) {
            errorHandler.handleError(e, "ContactService.addToFavorites")
        }
    }

    // Get favorite contacts
    suspend fun getFavoriteContacts(userId: String): List<Contact> {
        return try {
            userRepository.getFavoriteContacts(userId).first()
        } catch (e: Exception) {
            errorHandler.handleError(e, "ContactService.getFavoriteContacts")
            emptyList()
        }
    }

    // Check if contact has UPI app installed
    private fun checkIfHasUpiApp(phoneNumber: String): Boolean {
        // This is a simplified check - in real app, you'd check installed UPI apps
        // For demo purposes, assume all contacts have UPI apps
        return true
    }

    // Generate UPI ID from phone number
    private fun generateUpiId(phoneNumber: String): String {
        val cleanNumber = phoneNumber.replace("[^0-9]".toRegex(), "")
        return "$cleanNumber@paytm" // Default to Paytm, but should be configurable
    }

    private fun getPhoneNumbers(contentResolver: ContentResolver, contactId: String): List<String> {
        val phoneNumbers = mutableListOf<String>()
        val phoneCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId),
            null
        )

        phoneCursor?.use {
            while (it.moveToNext()) {
                val phoneNumber = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                phoneNumbers.add(phoneNumber)
            }
        }

        return phoneNumbers
    }
}
