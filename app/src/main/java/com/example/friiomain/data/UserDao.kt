package com.example.friiomain.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import androidx.room.*


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Delete
    suspend fun delete(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET password = :newPassword WHERE email = :email")
    suspend fun updatePassword(email: String, newPassword: String)

    @Query("UPDATE users SET username = :username WHERE email = :email")
    suspend fun updateUsernameByEmail(email: String, username: String)

    @Query("UPDATE users SET preferences = :prefs WHERE email = :email")
    suspend fun updatePreferences(email: String, prefs: String)

    @Query("UPDATE users SET avatarBase64 = :avatar WHERE email = :email")
    suspend fun updateAvatar(email: String, avatar: String?)

}
