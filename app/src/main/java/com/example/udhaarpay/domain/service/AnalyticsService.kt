package com.example.udhaarpay.domain.service

import com.example.udhaarpay.data.model.Transaction
import com.example.udhaarpay.data.model.TransactionType
import com.example.udhaarpay.data.repository.TransactionRepository
import com.example.udhaarpay.utils.ErrorHandler
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class AnalyticsService @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val errorHandler: ErrorHandler
) {

    // Get spending analytics
    suspend fun getSpendingAnalytics(userId: String, days: Int = 30): SpendingAnalytics {
        return try {
            val endDate = Date()
            val startDate = Date(endDate.time - (days * 24 * 60 * 60 * 1000L))

            val transactions = transactionRepository.getTransactionsInDateRange(
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                isDebit = true,
                status = com.example.udhaarpay.data.model.TransactionStatus.SUCCESS
            )

            val totalSpent = transactions.sumOf { it.amount }
            val transactionCount = transactions.size
            val averageTransaction = if (transactionCount > 0) totalSpent / transactionCount else 0.0

            // Category breakdown
            val categoryBreakdown = transactions.groupBy { it.category }
                .mapValues { entry ->
                    val amount = entry.value.sumOf { it.amount }
                    val percentage = if (totalSpent > 0) (amount / totalSpent) * 100 else 0.0
                    CategorySpending(entry.key, amount, percentage, entry.value.size)
                }
                .values
                .sortedByDescending { it.amount }

            // Top spending category
            val topCategory = categoryBreakdown.firstOrNull()?.category ?: "None"

            // Daily spending pattern
            val dailySpending = transactions.groupBy { transaction ->
                val calendar = Calendar.getInstance()
                calendar.time = transaction.timestamp
                "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
            }.mapValues { entry -> entry.value.sumOf { it.amount } }

            // Weekly comparison
            val weeklySpending = calculateWeeklySpending(transactions)

            SpendingAnalytics(
                totalSpent = totalSpent,
                transactionCount = transactionCount,
                averageTransaction = averageTransaction,
                topCategory = topCategory,
                categoryBreakdown = categoryBreakdown,
                dailySpending = dailySpending,
                weeklySpending = weeklySpending,
                periodDays = days
            )

        } catch (e: Exception) {
            errorHandler.handleError(e, "AnalyticsService.getSpendingAnalytics")
            SpendingAnalytics(0.0, 0, 0.0, "None", emptyList(), emptyMap(), emptyList(), days)
        }
    }

    // Get income analytics
    suspend fun getIncomeAnalytics(userId: String, days: Int = 30): IncomeAnalytics {
        return try {
            val endDate = Date()
            val startDate = Date(endDate.time - (days * 24 * 60 * 60 * 1000L))

            val transactions = transactionRepository.getTransactionsInDateRange(
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                isDebit = false,
                status = com.example.udhaarpay.data.model.TransactionStatus.SUCCESS
            )

            val totalIncome = transactions.sumOf { it.amount }
            val transactionCount = transactions.size
            val averageIncome = if (transactionCount > 0) totalIncome / transactionCount else 0.0

            // Source breakdown
            val sourceBreakdown = transactions.groupBy { it.senderName ?: "Unknown" }
                .mapValues { entry ->
                    val amount = entry.value.sumOf { it.amount }
                    val percentage = if (totalIncome > 0) (amount / totalIncome) * 100 else 0.0
                    IncomeSource(entry.key, amount, percentage, entry.value.size)
                }
                .values
                .sortedByDescending { it.amount }

            // Monthly trend
            val monthlyIncome = calculateMonthlyIncome(transactions)

            IncomeAnalytics(
                totalIncome = totalIncome,
                transactionCount = transactionCount,
                averageIncome = averageIncome,
                topSource = sourceBreakdown.firstOrNull()?.source ?: "None",
                sourceBreakdown = sourceBreakdown,
                monthlyIncome = monthlyIncome,
                periodDays = days
            )

        } catch (e: Exception) {
            errorHandler.handleError(e, "AnalyticsService.getIncomeAnalytics")
            IncomeAnalytics(0.0, 0, 0.0, "None", emptyList(), emptyList(), days)
        }
    }

    // Get budget analytics
    suspend fun getBudgetAnalytics(userId: String, monthlyBudget: Double): BudgetAnalytics {
        return try {
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)

            // Get transactions for current month
            calendar.set(currentYear, currentMonth, 1, 0, 0, 0)
            val startDate = calendar.time
            calendar.set(currentYear, currentMonth + 1, 1, 0, 0, 0)
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val endDate = calendar.time

            val transactions = transactionRepository.getTransactionsInDateRange(
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                isDebit = true,
                status = com.example.udhaarpay.data.model.TransactionStatus.SUCCESS
            )

            val spentAmount = transactions.sumOf { it.amount }
            val remainingBudget = monthlyBudget - spentAmount
            val budgetUsedPercentage = if (monthlyBudget > 0) (spentAmount / monthlyBudget) * 100 else 0.0

            // Days in current month
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val remainingDays = daysInMonth - currentDay + 1

            val dailyBudget = remainingBudget / remainingDays

            // Budget status
            val status = when {
                budgetUsedPercentage >= 100 -> BudgetStatus.EXCEEDED
                budgetUsedPercentage >= 80 -> BudgetStatus.WARNING
                else -> BudgetStatus.GOOD
            }

            // Category overspending
            val categoryBudget = monthlyBudget * 0.6 // Assume 60% for essentials
            val essentialsSpent = transactions
                .filter { isEssentialCategory(it.category) }
                .sumOf { it.amount }

            val essentialsOverBudget = essentialsSpent > categoryBudget

            BudgetAnalytics(
                monthlyBudget = monthlyBudget,
                spentAmount = spentAmount,
                remainingBudget = remainingBudget,
                budgetUsedPercentage = budgetUsedPercentage,
                dailyBudget = dailyBudget,
                status = status,
                essentialsOverBudget = essentialsOverBudget,
                daysRemaining = remainingDays
            )

        } catch (e: Exception) {
            errorHandler.handleError(e, "AnalyticsService.getBudgetAnalytics")
            BudgetAnalytics(monthlyBudget, 0.0, monthlyBudget, 0.0, monthlyBudget / 30, BudgetStatus.GOOD, false, 30)
        }
    }

    // Get rewards analytics
    suspend fun getRewardsAnalytics(userId: String, days: Int = 30): RewardsAnalytics {
        return try {
            val endDate = Date()
            val startDate = Date(endDate.time - (days * 24 * 60 * 60 * 1000L))

            val transactions = transactionRepository.getTransactionsInDateRange(
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                status = com.example.udhaarpay.data.model.TransactionStatus.SUCCESS
            )

            // Calculate cashback earned (assume 1% on debit transactions)
            val cashbackEarned = transactions
                .filter { it.isDebit }
                .sumOf { it.amount * 0.01 }

            // Rewards points (assume 1 point per rupee spent)
            val rewardsPoints = transactions
                .filter { it.isDebit }
                .sumOf { it.amount }
                .toInt()

            // Cashback by category
            val cashbackByCategory = transactions
                .filter { it.isDebit }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount * 0.01 } }

            // Monthly rewards trend
            val monthlyRewards = calculateMonthlyRewards(transactions)

            RewardsAnalytics(
                cashbackEarned = cashbackEarned,
                rewardsPoints = rewardsPoints,
                cashbackByCategory = cashbackByCategory,
                monthlyRewards = monthlyRewards,
                periodDays = days
            )

        } catch (e: Exception) {
            errorHandler.handleError(e, "AnalyticsService.getRewardsAnalytics")
            RewardsAnalytics(0.0, 0, emptyMap(), emptyList(), days)
        }
    }

    // Get payment behavior insights
    suspend fun getPaymentBehaviorInsights(userId: String): PaymentBehaviorInsights {
        return try {
            // Get last 90 days of transactions
            val endDate = Date()
            val startDate = Date(endDate.time - (90 * 24 * 60 * 60 * 1000L))

            val transactions = transactionRepository.getTransactionsInDateRange(
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                status = com.example.udhaarpay.data.model.TransactionStatus.SUCCESS
            )

            // Preferred payment method
            val paymentMethodUsage = transactions.groupBy { it.paymentMethod }
                .mapValues { it.value.size }
                .maxByOrNull { it.value }?.key?.name ?: "Unknown"

            // Average transaction amount
            val avgTransactionAmount = transactions.map { it.amount }.average()

            // Most active day of week
            val dayOfWeekActivity = transactions.groupBy { transaction ->
                val calendar = Calendar.getInstance()
                calendar.time = transaction.timestamp
                calendar.get(Calendar.DAY_OF_WEEK)
            }.mapValues { it.value.size }

            val mostActiveDay = dayOfWeekActivity.maxByOrNull { it.value }?.key ?: 1

            // Transaction frequency
            val transactionFrequency = transactions.size / 90.0 // transactions per day

            // Spending vs Income ratio
            val totalSpent = transactions.filter { it.isDebit }.sumOf { it.amount }
            val totalIncome = transactions.filter { !it.isDebit }.sumOf { it.amount }
            val spendingToIncomeRatio = if (totalIncome > 0) totalSpent / totalIncome else 0.0

            PaymentBehaviorInsights(
                preferredPaymentMethod = paymentMethodUsage,
                averageTransactionAmount = avgTransactionAmount,
                mostActiveDayOfWeek = mostActiveDay,
                transactionFrequency = transactionFrequency,
                spendingToIncomeRatio = spendingToIncomeRatio,
                totalTransactions = transactions.size
            )

        } catch (e: Exception) {
            errorHandler.handleError(e, "AnalyticsService.getPaymentBehaviorInsights")
            PaymentBehaviorInsights("Unknown", 0.0, 1, 0.0, 0.0, 0)
        }
    }

    // Helper functions
    private fun calculateWeeklySpending(transactions: List<Transaction>): List<WeeklySpending> {
        val calendar = Calendar.getInstance()
        return transactions.groupBy { transaction ->
            calendar.time = transaction.timestamp
            val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
            val year = calendar.get(Calendar.YEAR)
            Pair(year, weekOfYear)
        }.map { (week, txns) ->
            val amount = txns.sumOf { it.amount }
            WeeklySpending(week.first, week.second, amount)
        }.sortedBy { it.weekOfYear }
    }

    private fun calculateMonthlyIncome(transactions: List<Transaction>): List<MonthlyIncome> {
        val calendar = Calendar.getInstance()
        return transactions.groupBy { transaction ->
            calendar.time = transaction.timestamp
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            Pair(year, month)
        }.map { (month, txns) ->
            val amount = txns.sumOf { it.amount }
            MonthlyIncome(month.first, month.second, amount)
        }.sortedBy { it.month }
    }

    private fun calculateMonthlyRewards(transactions: List<Transaction>): List<MonthlyRewards> {
        val calendar = Calendar.getInstance()
        return transactions.groupBy { transaction ->
            calendar.time = transaction.timestamp
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            Pair(year, month)
        }.map { (month, txns) ->
            val cashback = txns.filter { it.isDebit }.sumOf { it.amount * 0.01 }
            val points = txns.filter { it.isDebit }.sumOf { it.amount }.toInt()
            MonthlyRewards(month.first, month.second, cashback, points)
        }.sortedBy { it.month }
    }

    private fun isEssentialCategory(category: String): Boolean {
        val essentialCategories = listOf("Food", "Transport", "Utilities", "Healthcare", "Education")
        return essentialCategories.any { category.contains(it, ignoreCase = true) }
    }
}

// Data classes for analytics
data class SpendingAnalytics(
    val totalSpent: Double,
    val transactionCount: Int,
    val averageTransaction: Double,
    val topCategory: String,
    val categoryBreakdown: List<CategorySpending>,
    val dailySpending: Map<String, Double>,
    val weeklySpending: List<WeeklySpending>,
    val periodDays: Int
)

data class CategorySpending(
    val category: String,
    val amount: Double,
    val percentage: Double,
    val transactionCount: Int
)

data class WeeklySpending(
    val year: Int,
    val weekOfYear: Int,
    val amount: Double
)

data class IncomeAnalytics(
    val totalIncome: Double,
    val transactionCount: Int,
    val averageIncome: Double,
    val topSource: String,
    val sourceBreakdown: List<IncomeSource>,
    val monthlyIncome: List<MonthlyIncome>,
    val periodDays: Int
)

data class IncomeSource(
    val source: String,
    val amount: Double,
    val percentage: Double,
    val transactionCount: Int
)

data class MonthlyIncome(
    val year: Int,
    val month: Int,
    val amount: Double
)

data class BudgetAnalytics(
    val monthlyBudget: Double,
    val spentAmount: Double,
    val remainingBudget: Double,
    val budgetUsedPercentage: Double,
    val dailyBudget: Double,
    val status: BudgetStatus,
    val essentialsOverBudget: Boolean,
    val daysRemaining: Int
)

enum class BudgetStatus {
    GOOD,
    WARNING,
    EXCEEDED
}

data class RewardsAnalytics(
    val cashbackEarned: Double,
    val rewardsPoints: Int,
    val cashbackByCategory: Map<String, Double>,
    val monthlyRewards: List<MonthlyRewards>,
    val periodDays: Int
)

data class MonthlyRewards(
    val year: Int,
    val month: Int,
    val cashback: Double,
    val points: Int
)

data class PaymentBehaviorInsights(
    val preferredPaymentMethod: String,
    val averageTransactionAmount: Double,
    val mostActiveDayOfWeek: Int,
    val transactionFrequency: Double,
    val spendingToIncomeRatio: Double,
    val totalTransactions: Int
)
