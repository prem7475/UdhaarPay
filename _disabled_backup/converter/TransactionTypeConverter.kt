package com.example.udhaarpay.data.local.converter

import androidx.room.TypeConverter
import com.example.udhaarpay.data.model.TransactionType

class TransactionTypeConverter {
    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? {
        return value?.let { TransactionType.valueOf(it) }
    }
}
