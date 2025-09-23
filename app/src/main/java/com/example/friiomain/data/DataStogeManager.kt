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
        private val USER_PREFERENCES_KEY = stringPreferencesKey("user_preferences")
    }

    // читаем username
    val userUsername: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_USERNAME_KEY]
    }

    // сохраняем username
    suspend fun saveUserUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_USERNAME_KEY] = username
        }
    }

    // очищаем username
    suspend fun clearUserUsername() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_USERNAME_KEY)
        }
    }


    // читаем email
    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL_KEY]
    }

    // читаем имя
    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY]
    }

    // читаем предпочтения (список строк)
    val userPreferences: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[USER_PREFERENCES_KEY]?.split(",") ?: emptyList()
    }

    // сохраняем email
    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
    }

    // сохраняем имя
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    // сохраняем предпочтения
    suspend fun saveUserPreferences(preferencesList: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[USER_PREFERENCES_KEY] = preferencesList.joinToString(",")
        }
    }

    // очищаем email
    suspend fun clearUserEmail() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_EMAIL_KEY)
        }
    }

    // очищаем имя
    suspend fun clearUserName() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_NAME_KEY)
        }
    }

    // полностью очищаем всё (и имя, и email, и preferences)
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

