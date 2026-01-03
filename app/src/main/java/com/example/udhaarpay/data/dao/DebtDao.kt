package com.example.udhaarpay.data.dao

import androidx.room.*
import com.example.udhaarpay.data.model.Debt
import com.example.udhaarpay.data.model.DebtType
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: Debt)
    
    @Update
    suspend fun updateDebt(debt: Debt)
    
    @Delete
    suspend fun deleteDebt(debt: Debt)
    
    @Query("SELECT * FROM debts WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserDebts(userId: Int): Flow<List<Debt>>
    
    @Query("SELECT * FROM debts WHERE userId = :userId AND debtType = :type ORDER BY createdAt DESC")
    fun getUserDebtsByType(userId: Int, type: DebtType): Flow<List<Debt>>
    
    @Query("SELECT * FROM debts WHERE id = :debtId")
    suspend fun getDebtById(debtId: Int): Debt?
    
    @Query("SELECT * FROM debts WHERE userId = :userId AND isSettled = 0 ORDER BY createdAt DESC")
    fun getPendingDebts(userId: Int): Flow<List<Debt>>
    
    @Query("SELECT COUNT(*) FROM debts WHERE userId = :userId AND isSettled = 0")
    fun getPendingDebtCount(userId: Int): Flow<Int>
    
    @Query("SELECT SUM(amount) FROM debts WHERE userId = :userId AND debtType = 'LENT_TO' AND isSettled = 0")
    fun getTotalLent(userId: Int): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM debts WHERE userId = :userId AND debtType = 'BORROWED_FROM' AND isSettled = 0")
    fun getTotalBorrowed(userId: Int): Flow<Double?>
    
    @Query("SELECT SUM(remainingAmount) FROM debts WHERE userId = :userId AND debtType = 'LENT_TO' AND isSettled = 0")
    fun getNetLent(userId: Int): Flow<Double?>
    
    @Query("SELECT SUM(remainingAmount) FROM debts WHERE userId = :userId AND debtType = 'BORROWED_FROM' AND isSettled = 0")
    fun getNetBorrowed(userId: Int): Flow<Double?>
    
    @Query("UPDATE debts SET isSettled = 1, settledAmount = :amount, remainingAmount = 0, updatedAt = :timestamp WHERE id = :debtId")
    suspend fun settleDebt(debtId: Int, amount: Double, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE debts SET remainingAmount = remainingAmount - :amount, updatedAt = :timestamp WHERE id = :debtId")
    suspend fun partialSettlement(debtId: Int, amount: Double, timestamp: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM debts WHERE userId = :userId")
    suspend fun deleteAllUserDebts(userId: Int)
}
