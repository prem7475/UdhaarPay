package com.udhaarpay.app.ui.navigation

sealed class AppRoute(val route: String) {
    data object Home : AppRoute("home")
    data object Payments : AppRoute("payments")
    data object Tickets : AppRoute("tickets")
    data object Invest : AppRoute("invest")
    data object InvestNews : AppRoute("invest_news")
    data object Account : AppRoute("account")

    data object ScanPay : AppRoute("scan_pay")
    data object BillPayments : AppRoute("pay_bills")
    data object MobileRecharge : AppRoute("mobile_recharge")
    data object CreditCard : AppRoute("credit_card")
    data object BankAccounts : AppRoute("bank_accounts")
    data object Insurance : AppRoute("insurance")
    data object Debt : AppRoute("debt")
    data object Expenses : AppRoute("expenses")
    data object Profile : AppRoute("profile")
    data object WalletManagement : AppRoute("wallet_management")
    data object Reminders : AppRoute("reminders")
    data object Support : AppRoute("support")
    data object Transactions : AppRoute("transactions")
    data object Offers : AppRoute("offers")
    data object SendMoney : AppRoute("send_money")
    data object RequestMoney : AppRoute("request_money")
    data object LinkUpi : AppRoute("link_upi")
    data object Passbook : AppRoute("passbook/{accountId}")

    companion object {
        fun passbook(accountId: Long): String = "passbook/$accountId"
    }
}

data class DrawerItem(
    val title: String,
    val route: String
)
