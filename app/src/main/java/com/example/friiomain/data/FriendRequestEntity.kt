package com.example.friiomain.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_requests")
data class FriendRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromEmail: String,
    val toEmail: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending",
    val username: String,
    val avatarBase64: String? = null
)
