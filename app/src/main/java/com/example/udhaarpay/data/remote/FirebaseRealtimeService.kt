package com.example.udhaarpay.data.remote

import com.example.udhaarpay.data.model.Transaction
import com.example.udhaarpay.utils.ErrorHandler
// Removed incorrect import for kotlin.Result
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRealtimeService @Inject constructor(
    private val errorHandler: ErrorHandler
) {

    private val database = FirebaseDatabase.getInstance().apply {
        setPersistenceEnabled(true)
    }

    private val usersRef = database.getReference("users")
    private val transactionsRef = database.getReference("transactions")
    private val onlineStatusRef = database.getReference(".info/connected")

    val connectionStatus: Flow<Boolean> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                trySend(connected)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(false)
            }
        }

        onlineStatusRef.addValueEventListener(listener)
        awaitClose { onlineStatusRef.removeEventListener(listener) }
    }

    fun observeUserBalance(userId: String): Flow<Double> = callbackFlow {
        val balanceRef = usersRef.child(userId).child("walletBalance")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val balance = snapshot.getValue(Double::class.java) ?: 0.0
                trySend(balance)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        balanceRef.addValueEventListener(listener)
        awaitClose { balanceRef.removeEventListener(listener) }
    }

    fun observeUserTransactions(userId: String): Flow<List<Transaction>> = callbackFlow {
        val userTransactionsRef = transactionsRef.orderByChild("userId").equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = mutableListOf<Transaction>()
                for (childSnapshot in snapshot.children) {
                    childSnapshot.getValue(Transaction::class.java)?.let { transactions.add(it) }
                }
                trySend(transactions.sortedByDescending { it.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        userTransactionsRef.addValueEventListener(listener)
        awaitClose { userTransactionsRef.removeEventListener(listener) }
    }

    fun updateBalanceAtomically(userId: String, amount: Double): Flow<Double> = callbackFlow {
        val balanceRef = usersRef.child(userId).child("walletBalance")

        balanceRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): com.google.firebase.database.Transaction.Result {
                val currentBalance = mutableData.getValue(Double::class.java) ?: 0.0
                val newBalance = currentBalance + amount
                if (newBalance < 0) return com.google.firebase.database.Transaction.abort()
                mutableData.value = newBalance
                return com.google.firebase.database.Transaction.success(mutableData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (error != null) {
                    close(error.toException())
                } else if (committed) {
                    val newBalance = currentData?.getValue(Double::class.java) ?: 0.0
                    trySend(newBalance)
                } else {
                    close(Exception("Insufficient funds"))
                }
            }
        })

        awaitClose { }
    }
}
