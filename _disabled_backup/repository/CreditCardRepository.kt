package com.example.udhaarpay.data.repository

import com.example.udhaarpay.data.local.dao.CreditCardDao
import com.example.udhaarpay.data.model.CreditCard
import com.example.udhaarpay.utils.CardValidator
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditCardRepository @Inject constructor(
    private val creditCardDao: CreditCardDao
) {
    
    fun getUserCards(userId: String): Flow<List<CreditCard>> {
        return creditCardDao.getUserCards(userId)
    }
    
    suspend fun getDefaultCard(userId: String): CreditCard? {
        return creditCardDao.getDefaultCard(userId)
    }
    
    suspend fun addCard(
        userId: String,
        cardNumber: String,
        cardholderName: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvv: String
    ): Result<CreditCard> {
        return try {
            // Validate RuPay card
            if (!CardValidator.isValidRuPayCard(cardNumber)) {
                return Result.failure(
                    Exception("Invalid card. Only RuPay cards are accepted.")
                )
            }
            
            // Validate expiry
            if (!CardValidator.isValidExpiry(expiryMonth, expiryYear)) {
                return Result.failure(
                    Exception("Card has expired or invalid expiry date.")
                )
            }
            
            // Validate CVV
            if (!CardValidator.isValidCVV(cvv)) {
                return Result.failure(
                    Exception("Invalid CVV. Must be 3-4 digits.")
                )
            }
            
            val lastFourDigits = cardNumber.replace("\\s".toRegex(), "").takeLast(4)
            val maskedCVV = CardValidator.maskCVV(cvv)
            val cardType = CardValidator.getCardType(cardNumber)
            
            // Determine issuer bank from card number
            val issuerBank = getIssuerBank(cardNumber)
            
            val card = CreditCard(
                userId = userId,
                cardNumber = CardValidator.maskCardNumber(cardNumber),
                cardNumberFull = cardNumber,
                cardholderName = cardholderName,
                expiryMonth = expiryMonth,
                expiryYear = expiryYear,
                cvv = maskedCVV,
                cardType = cardType,
                lastFourDigits = lastFourDigits,
                issuerBank = issuerBank
            )
            
            val cardId = creditCardDao.insertCard(card)
            Result.success(card.copy(id = cardId))
        } catch (e: Exception) {
            Timber.e(e, "Error adding credit card")
            Result.failure(e)
        }
    }
    
    suspend fun setDefaultCard(cardId: Long): Result<Unit> {
        return try {
            val card = creditCardDao.getCardById(cardId) ?: 
                return Result.failure(Exception("Card not found"))
            
            creditCardDao.clearDefaultFlag(card.userId)
            creditCardDao.setDefaultCard(cardId)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error setting default card")
            Result.failure(e)
        }
    }
    
    suspend fun deleteCard(cardId: Long): Result<Unit> {
        return try {
            val card = creditCardDao.getCardById(cardId) ?:
                return Result.failure(Exception("Card not found"))
            
            creditCardDao.deleteCard(card)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting card")
            Result.failure(e)
        }
    }
    
    suspend fun updateCard(card: CreditCard): Result<Unit> {
        return try {
            creditCardDao.updateCard(card)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error updating card")
            Result.failure(e)
        }
    }
    
    private fun getIssuerBank(cardNumber: String): String {
        // This is a simplified mapping. In production, use BIN database
        val bin = cardNumber.take(6)
        
        return when {
            bin.startsWith("508") || bin.startsWith("509") || bin.startsWith("510") -> "HDFC Bank"
            bin.startsWith("518") || bin.startsWith("520") -> "ICICI Bank"
            bin.startsWith("521") || bin.startsWith("522") -> "Axis Bank"
            bin.startsWith("523") -> "Kotak Bank"
            bin.startsWith("524") -> "SBI"
            bin.startsWith("526") -> "Federal Bank"
            bin.startsWith("65") -> "Union Bank"
            bin.startsWith("66") -> "Andhra Bank"
            else -> "RuPay Bank"
        }
    }
}
