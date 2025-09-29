package com.example.friiomain.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {

    companion object {
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_USERNAME_KEY = stringPreferencesKey("user_username")
        private val USER_PASSWORD_KEY = stringPreferencesKey("user_password")
        private val USER_PREFERENCES_KEY = stringPreferencesKey("user_preferences")
    }

    // чтение и запись предпочтений
    val userPreferences: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[USER_PREFERENCES_KEY] ?: ""
        if (raw.isBlank()) emptyList() else raw.split(",").map { it.trim() }
    }

    suspend fun saveUserPreferences(preferences: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[USER_PREFERENCES_KEY] = preferences.joinToString(", ")
        }
    }

    // остальные данные
    val userUsername: Flow<String> = context.dataStore.data.map { it[USER_USERNAME_KEY] ?: "" }
    suspend fun saveUserUsername(username: String) {
        context.dataStore.edit { it[USER_USERNAME_KEY] = username }
    }

    val userEmail: Flow<String> = context.dataStore.data.map { it[USER_EMAIL_KEY] ?: "" }
    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { it[USER_EMAIL_KEY] = email }
    }

    val userName: Flow<String> = context.dataStore.data.map { it[USER_NAME_KEY] ?: "" }
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { it[USER_NAME_KEY] = name }
    }

    val userPassword: Flow<String> = context.dataStore.data.map { it[USER_PASSWORD_KEY] ?: "" }
    suspend fun saveUserPassword(password: String) {
        context.dataStore.edit { it[USER_PASSWORD_KEY] = password }
    }

    // очистка всех данных
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}



