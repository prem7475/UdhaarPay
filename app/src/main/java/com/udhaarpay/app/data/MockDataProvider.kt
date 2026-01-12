package com.udhaarpay.app.data

import com.udhaarpay.app.data.local.entities.*

object MockDataProvider {
    val userProfiles = listOf(
        UserProfile(
            userId = "1",
            fullName = "Amit Kumar",
            email = "amit@example.com",
            phone = "+911234567890",
            dateOfBirth = 632448000000L, // 1990-01-01 in millis
            gender = "Male",
            address = "Delhi, India",
            city = "Delhi",
            state = "Delhi",
            pincode = "110001",
            profilePhotoUrl = "https://randomuser.me/api/portraits/men/1.jpg",
            panNumber = "ABCDE1234F",
            aadhaarNumber = "123412341234",
            kycStatus = true,
            kycDate = 1672531200000L // 2023-01-01 in millis
        ),
        UserProfile(
            userId = "2",
            fullName = "Priya Sharma",
            email = "priya@example.com",
            phone = "+919876543210",
            dateOfBirth = 705859200000L, // 1992-05-10 in millis
            gender = "Female",
            address = "Mumbai, India",
            city = "Mumbai",
            state = "Maharashtra",
            pincode = "400001",
            profilePhotoUrl = "https://randomuser.me/api/portraits/women/2.jpg",
            panNumber = "PQRSX5678Y",
            aadhaarNumber = "432143214321",
            kycStatus = true,
            kycDate = 1672531200000L
        )
    )
    val bankAccounts = listOf(
        BankAccount(
            accountId = 1L,
            bankName = "SBI",
            accountNumber = "1234567890",
            ifscCode = "SBIN0001234",
            accountType = "Savings",
            balance = 15000.0,
            nickname = "Salary Account",
            addedDate = 1672531200000L
        ),
        BankAccount(
            accountId = 2L,
            bankName = "HDFC",
            accountNumber = "9876543210",
            ifscCode = "HDFC0005678",
            accountType = "Current",
            balance = 25000.0,
            nickname = "Business Account",
            addedDate = 1672531200000L
        )
    )
    val creditCards = listOf(
        CreditCard(
            cardId = 1L,
            cardNumber = "4111111111111111",
            cardType = "Visa",
            issuer = "SBI",
            balance = 10000.0,
            limit = 50000.0,
            expiry = "12/28",
            status = "Active",
            upiLinked = true
        ),
        CreditCard(
            cardId = 2L,
            cardNumber = "5555555555554444",
            cardType = "Mastercard",
            issuer = "HDFC",
            balance = 15000.0,
            limit = 60000.0,
            expiry = "11/27",
            status = "Active",
            upiLinked = true
        )
    )
    val upiPayments = listOf(
        UPIPayment(
            transactionId = 1L,
            senderUPI = "amit@upi",
            recipientUPI = "priya@upi",
            amount = 1000.0,
            date = 1640995200000L,
            message = "Rent",
            status = "Success",
            type = "sent"
        ),
        UPIPayment(
            transactionId = 2L,
            senderUPI = "priya@upi",
            recipientUPI = "amit@upi",
            amount = 500.0,
            date = 1641081600000L,
            message = "Groceries",
            status = "Success",
            type = "sent"
        )
    )
    val debts = listOf(
        Debt(
            debtId = 1L,
            personName = "Amit Kumar",
            amount = 2000.0,
            type = "given",
            date = 1641168000000L,
            reason = "Dinner",
            status = "pending",
            settledDate = null,
            amountSettled = null
        ),
        Debt(
            debtId = 2L,
            personName = "Priya Sharma",
            amount = 1500.0,
            type = "taken",
            date = 1641254400000L,
            reason = "Cab",
            status = "settled",
            settledDate = 1641340800000L,
            amountSettled = 1500.0
        )
    )
    val expenses = listOf(
        Expense(
            expenseId = 1L,
            amount = 300.0,
            category = "Food",
            subCategory = null,
            account = "SBI",
            accountName = "Amit Kumar",
            description = "Lunch",
            date = 1641340800000L,
            month = "January",
            receiptUrl = null
        ),
        Expense(
            expenseId = 2L,
            amount = 1200.0,
            category = "Shopping",
            subCategory = null,
            account = "HDFC",
            accountName = "Priya Sharma",
            description = "Clothes",
            date = 1641427200000L,
            month = "January",
            receiptUrl = null
        )
    )
    val tickets = listOf(
        Ticket(
            ticketId = 1L,
            ticketType = "Flight",
            movieName = null,
            destination = "DEL-MUM",
            cinema = null,
            provider = "IndiGo",
            date = 1641513600000L,
            seats = "1A",
            amount = 5000.0,
            status = "Confirmed"
        ),
        Ticket(
            ticketId = 2L,
            ticketType = "Train",
            movieName = null,
            destination = "MUM-DEL",
            cinema = null,
            provider = "IRCTC",
            date = 1641600000000L,
            seats = "S2",
            amount = 1500.0,
            status = "Pending"
        )
    )
    val investments = listOf(
        Investment(
            investmentId = 1L,
            brokerName = "Zerodha",
            fundName = "Stocks",
            type = "sip",
            amount = 10000.0,
            frequency = "monthly",
            date = 1641686400000L,
            currentValue = 12000.0,
            returns = 2000.0
        ),
        Investment(
            investmentId = 2L,
            brokerName = "Groww",
            fundName = "Mutual Fund",
            type = "mutual",
            amount = 15000.0,
            frequency = "monthly",
            date = 1641772800000L,
            currentValue = 18000.0,
            returns = 3000.0
        )
    )
    val insurances = listOf(
        Insurance(
            policyId = 1L,
            policyType = "Health",
            provider = "LIC",
            premium = 5000.0,
            startDate = 1641859200000L,
            expiryDate = 1644451200000L,
            status = "Active",
            coverage = "Comprehensive"
        ),
        Insurance(
            policyId = 2L,
            policyType = "Life",
            provider = "HDFC Life",
            premium = 8000.0,
            startDate = 1641945600000L,
            expiryDate = 1644537600000L,
            status = "Active",
            coverage = "Term"
        )
    )
}
