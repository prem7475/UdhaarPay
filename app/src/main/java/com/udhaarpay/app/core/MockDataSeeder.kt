package com.udhaarpay.app.core

import com.udhaarpay.app.data.MockDataProvider
import com.udhaarpay.app.data.local.dao.NFCTransactionDao
import com.udhaarpay.app.data.local.dao.PaperTradingDao
import com.udhaarpay.app.data.local.entities.NFCTransactionEntity
import com.udhaarpay.app.data.local.entities.PaperTradingAccount
import com.udhaarpay.app.data.local.entities.Trade
import com.udhaarpay.app.repository.BankAccountRepository
import com.udhaarpay.app.repository.CreditCardRepository
import com.udhaarpay.app.repository.DebtRepository
import com.udhaarpay.app.repository.ExpenseRepository
import com.udhaarpay.app.repository.NFCTransactionRepository
import com.udhaarpay.app.repository.PaperTradingRepository
import com.udhaarpay.app.repository.TicketRepository
import com.udhaarpay.app.repository.UPIPaymentRepository
import com.udhaarpay.app.repository.UserProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class MockDataSeeder @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val creditCardRepository: CreditCardRepository,
    private val upiPaymentRepository: UPIPaymentRepository,
    private val expenseRepository: ExpenseRepository,
    private val debtRepository: DebtRepository,
    private val ticketRepository: TicketRepository,
    private val nfcTransactionRepository: NFCTransactionRepository,
    private val paperTradingRepository: PaperTradingRepository,
    private val paperTradingDao: PaperTradingDao,
    private val nfcTransactionDao: NFCTransactionDao
) {
    private val mutex = Mutex()

    suspend fun seedMockData() = mutex.withLock {
        seedUserProfiles()
        seedBankAccounts()
        seedCreditCards()
        seedUpiPayments()
        seedExpenses()
        seedDebts()
        seedTickets()
        seedNfcTransactions()
        seedPaperTrading()
    }

    private suspend fun seedUserProfiles() {
        if (userProfileRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.userProfiles.forEach { userProfileRepository.insert(it) }
    }

    private suspend fun seedBankAccounts() {
        if (bankAccountRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.bankAccounts.forEach { bankAccountRepository.insert(it) }
    }

    private suspend fun seedCreditCards() {
        if (creditCardRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.creditCards.forEach { creditCardRepository.insert(it) }
    }

    private suspend fun seedUpiPayments() {
        if (upiPaymentRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.upiPayments.forEach { upiPaymentRepository.insert(it) }
    }

    private suspend fun seedExpenses() {
        if (expenseRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.expenses.forEach { expenseRepository.insert(it) }
    }

    private suspend fun seedDebts() {
        if (debtRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.debts.forEach { debtRepository.insert(it) }
    }

    private suspend fun seedTickets() {
        if (ticketRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.tickets.forEach { ticketRepository.insert(it) }
    }

    private suspend fun seedNfcTransactions() {
        if (nfcTransactionDao.getAll().first().isNotEmpty()) return
        val seedTime = System.currentTimeMillis()
        nfcTransactionRepository.insert(
            NFCTransactionEntity(
                transactionRef = "NFC${seedTime}",
                cardId = MockDataProvider.creditCards.first().cardId,
                cardLast4 = MockDataProvider.creditCards.first().cardNumber.takeLast(4),
                amount = 1299.0,
                merchant = "Premium Mart",
                timestamp = seedTime - 86_400_000L * 2,
                status = "success",
                rewardEarned = 13.0
            )
        )
    }

    private suspend fun seedPaperTrading() {
        paperTradingRepository.ensureAccountExists()
        val account = paperTradingDao.getAccountOnce()
        if (account == null) {
            paperTradingDao.insertAccount(
                PaperTradingAccount(
                    virtualBalance = PaperTradingRepository.STARTING_BALANCE,
                    totalInvested = 0.0,
                    totalProfitLoss = 0.0,
                    createdDate = System.currentTimeMillis()
                )
            )
        }

        if (paperTradingDao.getOpenTradesSnapshot().isNotEmpty() || paperTradingDao.getClosedTradesSnapshot().isNotEmpty()) {
            return
        }

        val now = System.currentTimeMillis()
        paperTradingDao.insertTrade(
            Trade(
                stockSymbol = "RELIANCE",
                companyName = "Reliance Industries",
                tradeType = PaperTradingRepository.TYPE_BUY,
                quantity = 8,
                entryPrice = 2898.0,
                currentPrice = 2938.0,
                profitLoss = 320.0,
                tradeStatus = PaperTradingRepository.STATUS_OPEN,
                timestamp = now - 86_400_000L * 3
            )
        )
        paperTradingDao.insertTrade(
            Trade(
                stockSymbol = "TCS",
                companyName = "Tata Consultancy Services",
                tradeType = PaperTradingRepository.TYPE_CALL,
                quantity = 12,
                entryPrice = 4210.0,
                currentPrice = 4265.0,
                profitLoss = 660.0,
                tradeStatus = PaperTradingRepository.STATUS_OPEN,
                timestamp = now - 86_400_000L * 2
            )
        )
        paperTradingDao.insertTrade(
            Trade(
                stockSymbol = "SBIN",
                companyName = "State Bank of India",
                tradeType = PaperTradingRepository.TYPE_PUT,
                quantity = 18,
                entryPrice = 773.0,
                currentPrice = 761.0,
                profitLoss = 216.0,
                tradeStatus = PaperTradingRepository.STATUS_CLOSED,
                timestamp = now - 86_400_000L * 8
            )
        )
    }
}
