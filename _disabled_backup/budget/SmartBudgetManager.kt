package com.example.udhaarpay.data.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.udhaarpay.data.model.Transaction
import com.example.udhaarpay.data.model.TransactionType
import com.example.udhaarpay.data.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartBudgetManager @Inject constructor(
    private val transactionRepository: TransactionRepository
) {

    private val _budgetStatus = MutableLiveData<Map<String, BudgetStatus>>()
    val budgetStatus: LiveData<Map<String, BudgetStatus>> = _budgetStatus

    private val _budgetAlerts = MutableLiveData<List<BudgetAlert>>()
    val budgetAlerts: LiveData<List<BudgetAlert>> = _budgetAlerts

    private val _budgetGoals = MutableLiveData<List<BudgetGoal>>()
    val budgetGoals: LiveData<List<BudgetGoal>> = _budgetGoals

    // Default budget categories and limits
    private val defaultBudgets = mapOf(
        "Food & Dining" to 8000.0,
        "Transportation" to 3000.0,
        "Entertainment" to 4000.0,
        "Shopping" to 6000.0,
        "Bills & Utilities" to 10000.0,
        "Healthcare" to 2000.0,
        "Education" to 3000.0,
        "Travel" to 5000.0,
        "Investments" to 5000.0,
        "Miscellaneous" to 2000.0
    )

    companion object {
        private const val TAG = "SmartBudgetManager"
        private const val WARNING_THRESHOLD = 0.8 // 80% of budget
        private const val CRITICAL_THRESHOLD = 0.95 // 95% of budget
    }

    /**
     * Set budget for a specific category
     */
    fun setBudget(category: String, monthlyLimit: Double) {
        // In a real implementation, this would be saved to database/preferences
        Timber.d(TAG, "Budget set for $category: ₹$monthlyLimit")
    }

    /**
     * Get current budget status for all categories
     */
    suspend fun getBudgetStatus(): Map<String, BudgetStatus> {
        try {
            val transactions = transactionRepository.getAllTransactions().first()
            val currentMonth = getCurrentMonth()

            val statusMap = mutableMapOf<String, BudgetStatus>()

            defaultBudgets.forEach { (category, limit) ->
                val spentThisMonth = calculateSpentThisMonth(transactions, category, currentMonth)
                val remaining = limit - spentThisMonth
                val percentageUsed = if (limit > 0) (spentThisMonth / limit) * 100 else 0.0

                statusMap[category] = BudgetStatus(
                    category = category,
                    monthlyLimit = limit,
                    spentThisMonth = spentThisMonth,
                    remaining = remaining.coerceAtLeast(0.0),
                    percentageUsed = percentageUsed.coerceAtMost(100.0),
                    status = determineBudgetStatus(percentageUsed)
                )
            }

            _budgetStatus.value = statusMap
            return statusMap

        } catch (e: Exception) {
            Timber.e(TAG, "Failed to get budget status", e)
            return emptyMap()
        }
    }

    /**
     * Check for budget alerts and generate notifications
     */
    suspend fun checkBudgetAlerts(): List<BudgetAlert> {
        try {
            val budgetStatus = getBudgetStatus()
            val alerts = mutableListOf<BudgetAlert>()

            budgetStatus.forEach { (category, status) ->
                when {
                    status.percentageUsed >= CRITICAL_THRESHOLD -> {
                        alerts.add(
                            BudgetAlert(
                                id = "${category}_critical_${System.currentTimeMillis()}",
                                category = category,
                                type = BudgetAlertType.CRITICAL,
                                message = "Critical: You've used ${String.format("%.1f", status.percentageUsed)}% of your $category budget",
                                amountOver = status.spentThisMonth - status.monthlyLimit,
                                suggestedAction = "Consider pausing non-essential spending in $category"
                            )
                        )
                    }
                    status.percentageUsed >= WARNING_THRESHOLD -> {
                        alerts.add(
                            BudgetAlert(
                                id = "${category}_warning_${System.currentTimeMillis()}",
                                category = category,
                                type = BudgetAlertType.WARNING,
                                message = "Warning: You've used ${String.format("%.1f", status.percentageUsed)}% of your $category budget",
                                amountOver = 0.0,
                                suggestedAction = "Monitor spending in $category closely"
                            )
                        )
                    }
                }
            }

            // Add overspending alerts
            budgetStatus.filter { it.value.spentThisMonth > it.value.monthlyLimit }
                .forEach { (category, status) ->
                    alerts.add(
                        BudgetAlert(
                            id = "${category}_overspend_${System.currentTimeMillis()}",
                            category = category,
                            type = BudgetAlertType.OVERSPEND,
                            message = "Overspent: ₹${String.format("%.2f", status.spentThisMonth - status.monthlyLimit)} over $category budget",
                            amountOver = status.spentThisMonth - status.monthlyLimit,
                            suggestedAction = "Review and adjust future spending in $category"
                        )
                    )
                }

            _budgetAlerts.value = alerts
            return alerts

        } catch (e: Exception) {
            Timber.e(TAG, "Failed to check budget alerts", e)
            return emptyList()
        }
    }

    /**
     * Create and manage budget goals
     */
    suspend fun createBudgetGoals(): List<BudgetGoal> {
        val goals = listOf(
            BudgetGoal(
                id = "reduce_eating_out",
                title = "Reduce Eating Out",
                description = "Cut down on restaurant expenses",
                targetReduction = 3000.0,
                currentProgress = 1500.0,
                category = "Food & Dining",
                deadline = getFutureDate(3), // 3 months
                status = BudgetGoalStatus.IN_PROGRESS
            ),
            BudgetGoal(
                id = "increase_savings",
                title = "Build Emergency Fund",
                description = "Save ₹50,000 for emergencies",
                targetReduction = 0.0, // This is a savings goal
                currentProgress = 15000.0,
                category = "Savings",
                deadline = getFutureDate(6),
                status = BudgetGoalStatus.IN_PROGRESS
            ),
            BudgetGoal(
                id = "optimize_transport",
                title = "Optimize Transportation",
                description = "Reduce transport costs by using public transport",
                targetReduction = 2000.0,
                currentProgress = 800.0,
                category = "Transportation",
                deadline = getFutureDate(2),
                status = BudgetGoalStatus.IN_PROGRESS
            )
        )

        _budgetGoals.value = goals
        return goals
    }

    /**
     * Get spending insights for budget optimization
     */
    suspend fun getSpendingInsights(): SpendingInsights {
        try {
            val transactions = transactionRepository.getAllTransactions().first()
            val currentMonth = getCurrentMonth()
            val lastMonth = getLastMonth()

            val currentMonthSpending = calculateTotalSpentThisMonth(transactions, currentMonth)
            val lastMonthSpending = calculateTotalSpentThisMonth(transactions, lastMonth)

            val changePercent = if (lastMonthSpending > 0) {
                ((currentMonthSpending - lastMonthSpending) / lastMonthSpending) * 100
            } else 0.0

            return SpendingInsights(
                currentMonthSpending = currentMonthSpending,
                lastMonthSpending = lastMonthSpending,
                changePercent = changePercent,
                topSpendingCategory = findTopSpendingCategory(transactions, currentMonth),
                dailyAverage = currentMonthSpending / 30.0,
                projectedMonthly = currentMonthSpending * (30.0 / getCurrentDayOfMonth())
            )

        } catch (e: Exception) {
            Timber.e(TAG, "Failed to get spending insights", e)
            return SpendingInsights()
        }
    }

    // Helper functions

    private fun calculateSpentThisMonth(transactions: List<Transaction>, category: String, monthKey: String): Double {
        return transactions
            .filter { transaction ->
                transaction.type in listOf(TransactionType.SEND_MONEY, TransactionType.PAY_BILLS, TransactionType.SCAN_PAY) &&
                transaction.category.equals(category, ignoreCase = true) &&
                getMonthKey(transaction.timestamp) == monthKey
            }
            .sumOf { it.amount }
    }

    private fun calculateTotalSpentThisMonth(transactions: List<Transaction>, monthKey: String): Double {
        return transactions
            .filter { transaction ->
                transaction.type in listOf(TransactionType.SEND_MONEY, TransactionType.PAY_BILLS, TransactionType.SCAN_PAY) &&
                getMonthKey(transaction.timestamp) == monthKey
            }
            .sumOf { it.amount }
    }

    private fun determineBudgetStatus(percentageUsed: Double): BudgetStatusType {
        return when {
            percentageUsed >= CRITICAL_THRESHOLD -> BudgetStatusType.CRITICAL
            percentageUsed >= WARNING_THRESHOLD -> BudgetStatusType.WARNING
            percentageUsed >= 0.5 -> BudgetStatusType.NORMAL
            else -> BudgetStatusType.GOOD
        }
    }

    private fun findTopSpendingCategory(transactions: List<Transaction>, monthKey: String): String {
        val categorySpending = transactions
            .filter { getMonthKey(it.timestamp) == monthKey }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { transaction -> transaction.amount } }

        return categorySpending.maxByOrNull { it.value }?.key ?: "No spending"
    }

    private fun getMonthKey(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}"
    }

    private fun getCurrentMonth(): String {
        return getMonthKey(Date())
    }

    private fun getLastMonth(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        return getMonthKey(calendar.time)
    }

    private fun getCurrentDayOfMonth(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }

    private fun getFutureDate(monthsFromNow: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, monthsFromNow)
        return calendar.time
    }

    // Data classes

    data class BudgetStatus(
        val category: String,
        val monthlyLimit: Double,
        val spentThisMonth: Double,
        val remaining: Double,
        val percentageUsed: Double,
        val status: BudgetStatusType
    )

    enum class BudgetStatusType {
        GOOD, NORMAL, WARNING, CRITICAL
    }

    data class BudgetAlert(
        val id: String,
        val category: String,
        val type: BudgetAlertType,
        val message: String,
        val amountOver: Double,
        val suggestedAction: String
    )

    enum class BudgetAlertType {
        WARNING, CRITICAL, OVERSPEND
    }

    data class BudgetGoal(
        val id: String,
        val title: String,
        val description: String,
        val targetReduction: Double,
        val currentProgress: Double,
        val category: String,
        val deadline: Date,
        val status: BudgetGoalStatus
    )

    enum class BudgetGoalStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED, EXPIRED
    }

    data class SpendingInsights(
        val currentMonthSpending: Double = 0.0,
        val lastMonthSpending: Double = 0.0,
        val changePercent: Double = 0.0,
        val topSpendingCategory: String = "No data",
        val dailyAverage: Double = 0.0,
        val projectedMonthly: Double = 0.0
    )
}
