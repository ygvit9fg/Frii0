package com.example.friiomain.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val preferredWeather: String? = null,
    val lastLogin: Long = System.currentTimeMillis(),
    val username: String? = null,
    val preferences: String? = null,
    val avatarBase64: String? = null
)
