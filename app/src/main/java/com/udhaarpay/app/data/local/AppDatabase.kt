


package com.udhaarpay.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.udhaarpay.app.data.local.entities.*
import com.udhaarpay.app.data.local.dao.UserProfileDao
import com.udhaarpay.app.data.local.dao.UPIPaymentDao
import com.udhaarpay.app.data.local.dao.DebtDao
import com.udhaarpay.app.data.local.dao.ExpenseDao
import com.udhaarpay.app.data.local.dao.TicketDao
import com.udhaarpay.app.data.local.dao.InvestmentDao
import com.udhaarpay.app.data.local.dao.InsuranceDao
import com.udhaarpay.app.data.local.dao.CreditCardDao
import com.udhaarpay.app.data.local.dao.BankAccountDao
import com.udhaarpay.app.data.local.dao.NFCTransactionDao
import com.udhaarpay.app.data.local.dao.PaperTradingDao

@Database(
	entities = [
		UserProfile::class,
		UPIPayment::class,
		Debt::class,
		Expense::class,
		Ticket::class,
		Investment::class,
		Insurance::class,
		CreditCard::class,
		BankAccount::class,
		NFCTransactionEntity::class,
		PaperTradingAccount::class,
		Trade::class
	],
	version = 7,
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userProfileDao(): UserProfileDao
	abstract fun upiPaymentDao(): UPIPaymentDao
	abstract fun debtDao(): DebtDao
	abstract fun expenseDao(): ExpenseDao
	abstract fun ticketDao(): TicketDao
	abstract fun investmentDao(): InvestmentDao
	abstract fun insuranceDao(): InsuranceDao
	abstract fun creditCardDao(): CreditCardDao
	abstract fun bankAccountDao(): BankAccountDao
	abstract fun nfcTransactionDao(): NFCTransactionDao
	abstract fun paperTradingDao(): PaperTradingDao
}

