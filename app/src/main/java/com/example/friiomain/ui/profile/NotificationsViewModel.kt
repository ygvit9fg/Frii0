package com.example.friiomain.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friiomain.data.NotificationEntity
import com.example.friiomain.data.NotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationsRepository
) : ViewModel() {

    val notifications: StateFlow<List<NotificationEntity>> =
        repository.getNotifications()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )


    fun addNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            repository.addNotification(notification)
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
