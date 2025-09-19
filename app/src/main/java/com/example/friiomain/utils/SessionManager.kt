package com.example.friiomain.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(email: String, name: String) {
        prefs.edit().apply {
            putString("email", email)
            putString("name", name)
            putBoolean("isLoggedIn", true)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("isLoggedIn", false)
    }

    fun getUserEmail(): String {
        return prefs.getString("email", "") ?: ""
    }

    fun getUserName(): String {
        return prefs.getString("name", "") ?: ""
    }

    // ✅ метод для выхода
    fun logout() {
        prefs.edit().clear().apply()
    }
}
