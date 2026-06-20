package com.udhaarpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhaarpay.app.data.MockDataProvider
import com.udhaarpay.app.data.local.entities.BankAccount
import com.udhaarpay.app.data.local.entities.CreditCard
import com.udhaarpay.app.data.local.entities.Debt
import com.udhaarpay.app.data.local.entities.Expense
import com.udhaarpay.app.data.local.entities.Insurance
import com.udhaarpay.app.data.local.entities.Investment
import com.udhaarpay.app.data.local.entities.Ticket
import com.udhaarpay.app.data.local.entities.UserProfile
import com.udhaarpay.app.repository.BankAccountRepository
import com.udhaarpay.app.repository.CreditCardRepository
import com.udhaarpay.app.repository.DebtRepository
import com.udhaarpay.app.repository.ExpenseRepository
import com.udhaarpay.app.repository.InsuranceRepository
import com.udhaarpay.app.repository.InvestmentRepository
import com.udhaarpay.app.repository.PaperTradingRepository
import com.udhaarpay.app.repository.TicketRepository
import com.udhaarpay.app.repository.UPIPaymentRepository
import com.udhaarpay.app.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AppBootstrapViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val creditCardRepository: CreditCardRepository,
    private val debtRepository: DebtRepository,
    private val expenseRepository: ExpenseRepository,
    private val ticketRepository: TicketRepository,
    private val investmentRepository: InvestmentRepository,
    private val insuranceRepository: InsuranceRepository,
    private val upiPaymentRepository: UPIPaymentRepository,
    private val paperTradingRepository: PaperTradingRepository
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private var started = false

    fun seedIfNeeded() {
        if (started) return
        started = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                seedProfiles()
                seedBankAccounts()
                seedCreditCards()
                seedDebts()
                seedExpenses()
                seedTickets()
                seedInvestments()
                seedInsurances()
                seedPayments()
                paperTradingRepository.ensureAccountExists()
            }
            _isReady.value = true
        }
    }

    private suspend fun seedProfiles() {
        if (userProfileRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.userProfiles.forEach { profile ->
            userProfileRepository.insert(profile)
        }
    }

    private suspend fun seedBankAccounts() {
        if (bankAccountRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.bankAccounts.forEach { account ->
            bankAccountRepository.insert(account)
        }
    }

    private suspend fun seedCreditCards() {
        if (creditCardRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.creditCards.forEach { card ->
            creditCardRepository.insert(card)
        }
    }

    private suspend fun seedDebts() {
        if (debtRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.debts.forEach { debt ->
            debtRepository.insert(debt)
        }
    }

    private suspend fun seedExpenses() {
        if (expenseRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.expenses.forEach { expense ->
            expenseRepository.insert(expense)
        }
    }

    private suspend fun seedTickets() {
        if (ticketRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.tickets.forEach { ticket ->
            ticketRepository.insert(ticket)
        }
    }

    private suspend fun seedInvestments() {
        if (investmentRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.investments.forEach { investment ->
            investmentRepository.insert(investment)
        }
    }

    private suspend fun seedInsurances() {
        if (insuranceRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.insurances.forEach { insurance ->
            insuranceRepository.insert(insurance)
        }
    }

    private suspend fun seedPayments() {
        if (upiPaymentRepository.getAll().first().isNotEmpty()) return
        MockDataProvider.upiPayments.forEach { payment ->
            upiPaymentRepository.insert(payment)
        }
    }
}
