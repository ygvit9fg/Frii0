package com.example.friiomain.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,   // "weather", "streak", "news"
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
