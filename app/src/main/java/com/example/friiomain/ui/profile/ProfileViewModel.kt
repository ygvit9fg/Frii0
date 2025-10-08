package com.example.friiomain.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.friiomain.data.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlinx.coroutines.launch



@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val userName: StateFlow<String> = dataStoreManager.userName
        .map { it ?: "" }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val userEmail: StateFlow<String> = dataStoreManager.userEmail
        .map { it ?: "" }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val userPreferences: StateFlow<List<String>> = dataStoreManager.userPreferences
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val userUsername: StateFlow<String> = dataStoreManager.userUsername
        .map { it ?: "" }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val userAvatar: StateFlow<String?> = dataStoreManager.userAvatar
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // --- Методы обновления ---
    fun updateUserName(newName: String) {
        viewModelScope.launch {
            dataStoreManager.saveUserName(newName)
        }
    }

    fun updateUserEmail(newEmail: String) {   // 🔥 добавлено
        viewModelScope.launch {
            dataStoreManager.saveUserEmail(newEmail)
        }
    }

    fun updateUserPreferences(prefs: List<String>) {
        viewModelScope.launch {
            dataStoreManager.saveUserPreferences(prefs)
        }
    }

    fun updateUserAvatar(avatarBase64: String?) {
        viewModelScope.launch {
            if (avatarBase64 != null) {
                dataStoreManager.saveUserAvatar(avatarBase64)
            } else {
                dataStoreManager.saveUserAvatar("") // очистка
            }
        }
    }

    fun updateUserPassword(newPassword: String) {
        viewModelScope.launch {
            dataStoreManager.saveUserPassword(newPassword)
        }
    }

    fun updateUserUsername(newUsername: String) {
        viewModelScope.launch {
            dataStoreManager.saveUserUsername(newUsername)
        }
    }


    fun clearAll() {
        viewModelScope.launch {
            dataStoreManager.clearAll()
        }
    }
}




class ProfileViewModelFactory(
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


