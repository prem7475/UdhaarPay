package com.example.udhaarpay.data.local.converter

import androidx.room.TypeConverter
import com.example.udhaarpay.data.model.AccountType

class AccountTypeConverter {
    @TypeConverter
    fun fromAccountType(type: AccountType?): String? = type?.name

    @TypeConverter
    fun toAccountType(value: String?): AccountType? = value?.let { AccountType.valueOf(it) }
}
