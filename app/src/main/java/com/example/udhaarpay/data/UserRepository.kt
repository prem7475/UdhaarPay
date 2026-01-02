package com.example.udhaarpay.data

import android.content.Context
import android.content.SharedPreferences

class UserRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUserName(name: String) {
        prefs.edit().putString("USER_NAME", name).apply()
    }

    fun getUserName(): String {
        return prefs.getString("USER_NAME", "User") ?: "User"
    }

    fun saveUserEmail(email: String) {
        prefs.edit().putString("USER_EMAIL", email).apply()
    }

    fun getUserEmail(): String {
        return prefs.getString("USER_EMAIL", "") ?: ""
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}