package com.example.friiomain.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,                        // имя
    val username: String? = null,            // ник (отличается от name)
    val email: String,                       // почта
    val password: String,                    // пароль
    val preferences: String? = null,         // список предпочтений (CSV строка: "cats,dogs,weather")
    val avatarBase64: String? = null,        // картинка в виде строки Base64
    val lastLogin: Long = System.currentTimeMillis(), // время последнего входа
)
