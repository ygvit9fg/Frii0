package com.example.friiomain.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(email: String, name: String) {
        sharedPreferences.edit().apply {
            putString("email", email)
            putString("name", name)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.contains("email")
    }

    fun getUserEmail(): String {
        return sharedPreferences.getString("email", "") ?: ""
    }

    fun getUserName(): String {
        return sharedPreferences.getString("name", "") ?: ""
    }

    fun clear() {
        sharedPreferences.edit().apply {
            clear()
            apply()
        }
    }
}

