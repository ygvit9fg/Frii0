package com.example.friiomain.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.friiomain.data.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStore: DataStoreManager
) : ViewModel() {

    val userName: StateFlow<String> = dataStore.userName
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val userEmail: StateFlow<String> = dataStore.userEmail
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val userUsername: StateFlow<String> = dataStore.userUsername
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val userPassword: StateFlow<String> = dataStore.userPassword
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val userPreferences: StateFlow<List<String>> = dataStore.userPreferences
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            dataStore.saveUserName(newName)
        }
    }

    fun updateUserPassword(newPassword: String) {
        viewModelScope.launch {
            dataStore.saveUserPassword(newPassword)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            dataStore.clearAll()
        }
    }

    fun updateUserPreferences(prefs: List<String>) {
        viewModelScope.launch {
            dataStore.saveUserPreferences(prefs)
        }
    }
}



class ProfileViewModelFactory(
    private val dataStore: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



