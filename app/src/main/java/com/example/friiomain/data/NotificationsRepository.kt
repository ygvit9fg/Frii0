package com.example.friiomain.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepository @Inject constructor(
    private val dao: NotificationDao
) {
    fun getNotifications(): Flow<List<NotificationEntity>> = dao.getAllNotifications()

    suspend fun addNotification(notification: NotificationEntity) {
        dao.insertNotification(notification)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
