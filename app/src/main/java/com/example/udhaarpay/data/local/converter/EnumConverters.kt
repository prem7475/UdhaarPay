package com.example.udhaarpay.data.local.converter

import androidx.room.TypeConverter
import com.example.udhaarpay.data.model.PaymentMethod
import com.example.udhaarpay.data.model.TransactionStatus
import com.example.udhaarpay.data.model.TransactionSubType

class EnumConverters {
    @TypeConverter
    fun fromTransactionSubType(value: String?) = value?.let { TransactionSubType.valueOf(it) }

    @TypeConverter
    fun toTransactionSubType(type: TransactionSubType?) = type?.name

    @TypeConverter
    fun fromTransactionStatus(value: String?) = value?.let { TransactionStatus.valueOf(it) }

    @TypeConverter
    fun toTransactionStatus(status: TransactionStatus?) = status?.name

    @TypeConverter
    fun fromPaymentMethod(value: String?) = value?.let { PaymentMethod.valueOf(it) }

    @TypeConverter
    fun toPaymentMethod(method: PaymentMethod?) = method?.name
}
