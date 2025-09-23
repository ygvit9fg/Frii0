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

