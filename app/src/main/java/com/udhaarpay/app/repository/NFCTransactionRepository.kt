package com.udhaarpay.app.repository

import com.udhaarpay.app.data.local.dao.NFCTransactionDao
import com.udhaarpay.app.data.local.entities.NFCTransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NFCTransactionRepository @Inject constructor(
    private val dao: NFCTransactionDao
) {
    fun getAll(): Flow<List<NFCTransactionEntity>> = dao.getAll()
    fun getByCardId(cardId: Long): Flow<List<NFCTransactionEntity>> = dao.getByCardId(cardId)
    suspend fun insert(transaction: NFCTransactionEntity): Long = dao.insert(transaction)
}
