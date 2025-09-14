package com.example.friiomain.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao{
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Query("select * from users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String , password: String):UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
}