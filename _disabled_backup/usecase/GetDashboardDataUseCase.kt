package com.example.udhaarpay.domain.usecase

import com.example.udhaarpay.data.local.dao.BankAccountDao
import com.example.udhaarpay.data.local.dao.CreditCardDao
import com.example.udhaarpay.data.local.dao.TransactionDao
import com.example.udhaarpay.data.local.dao.UserDao
import com.example.udhaarpay.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class DashboardData(
    val totalBalance: Double,
    val creditLimit: Double,
    val recentTransactions: List<TransactionEntity>
)

class GetDashboardDataUseCase @Inject constructor(
    private val bankAccountDao: BankAccountDao,
    private val creditCardDao: CreditCardDao,
    private val transactionDao: TransactionDao,
    private val userDao: UserDao
) {
    operator fun invoke(): Flow<DashboardData> {
        return combine(
            bankAccountDao.getAllBankAccounts(),
            creditCardDao.getAllCreditCards(),
            transactionDao.getAllTransactions(),
            userDao.getWallet()
        ) { bankAccounts, creditCards, transactions, wallet ->
            val bankBalance = bankAccounts.sumOf { it.balance }
            val walletBalance = wallet?.currentBalance ?: 0.0
            val totalBalance = bankBalance + walletBalance
            
            val totalCreditLimit = creditCards.sumOf { it.limit }
            
            val recentTransactions = transactions.take(5)
            
            DashboardData(
                totalBalance = totalBalance,
                creditLimit = totalCreditLimit,
                recentTransactions = recentTransactions
            )
        }
    }
}
