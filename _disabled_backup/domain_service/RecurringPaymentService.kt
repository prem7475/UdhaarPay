package com.example.udhaarpay.domain.service

import com.example.udhaarpay.data.dao.RecurringPaymentDao
import com.example.udhaarpay.data.model.*
import com.example.udhaarpay.data.repository.TransactionRepository
import com.example.udhaarpay.data.repository.UserRepository
import com.example.udhaarpay.utils.ErrorHandler
import com.example.udhaarpay.utils.Logger
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurringPaymentService @Inject constructor(
    private val recurringPaymentDao: RecurringPaymentDao,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val paymentService: PaymentService,
    private val notificationService: NotificationService,
    private val errorHandler: ErrorHandler,
    private val logger: Logger
) {

}
