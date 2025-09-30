package com.example.friiomain.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_PASSWORD_KEY = stringPreferencesKey("user_password")
        private val USER_USERNAME_KEY = stringPreferencesKey("user_username")
        private val USER_PREFERENCES_KEY = stringPreferencesKey("user_preferences")
        private val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
    }

    val userName: Flow<String> = context.dataStore.data.map { it[USER_NAME_KEY] ?: "" }
    val userEmail: Flow<String> = context.dataStore.data.map { it[USER_EMAIL_KEY] ?: "" }
    val userPassword: Flow<String> = context.dataStore.data.map { it[USER_PASSWORD_KEY] ?: "" }
    val userUsername: Flow<String> = context.dataStore.data.map { it[USER_USERNAME_KEY] ?: "" }
    val userPreferences: Flow<List<String>> =
        context.dataStore.data.map { it[USER_PREFERENCES_KEY]?.split(",") ?: emptyList() }

    val userAvatar: Flow<String?> = context.dataStore.data.map { it[USER_AVATAR_KEY] }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { it[USER_NAME_KEY] = name }
    }

    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { it[USER_EMAIL_KEY] = email }
    }

    suspend fun saveUserPassword(password: String) {
        context.dataStore.edit { it[USER_PASSWORD_KEY] = password }
    }

    suspend fun saveUserUsername(username: String) {
        context.dataStore.edit { it[USER_USERNAME_KEY] = username }
    }

    suspend fun saveUserPreferences(preferences: List<String>) {
        context.dataStore.edit { it[USER_PREFERENCES_KEY] = preferences.joinToString(",") }
    }

    suspend fun saveUserAvatar(avatarBase64: String) {
        context.dataStore.edit { it[USER_AVATAR_KEY] = avatarBase64 }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}




