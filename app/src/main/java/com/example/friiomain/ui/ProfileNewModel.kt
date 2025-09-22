package com.example.friiomain.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import com.example.friiomain.data.DataStoreManager

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val userName = dataStoreManager.userName.stateIn(viewModelScope, SharingStarted.Lazily, "")
    val userEmail = dataStoreManager.userEmail.stateIn(viewModelScope, SharingStarted.Lazily, "")
    val userPreferences = dataStoreManager.userPreferences.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
