package com.example.friiomain.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val preferences: String? = null,
    val avatarBase64: String? = null,
    val lastLogin: Long = System.currentTimeMillis()
)




