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

    // --- Друзья ---
    @Insert
    suspend fun insert(friend: FriendEntity)

    @Query("SELECT * FROM friends WHERE userEmail = :userEmail")
    suspend fun getFriendsForUser(userEmail: String): List<FriendEntity>

    @Query("DELETE FROM friends WHERE userEmail = :userEmail AND friendEmail = :friendEmail")
    suspend fun deleteFriend(userEmail: String, friendEmail: String)

    // --- Заявки ---
    @Insert
    suspend fun insertRequest(request: FriendRequestEntity)

    @Query("SELECT * FROM friend_requests WHERE toEmail = :userEmail")
    suspend fun getIncomingRequests(userEmail: String): List<FriendRequestEntity>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?


    @Query("DELETE FROM friend_requests WHERE id = :requestId")
    suspend fun deleteRequest(requestId: Int)


    @Query("""
    SELECT * FROM users 
    WHERE LOWER(username) LIKE LOWER(:query) 
       OR LOWER(email) LIKE LOWER(:query)
    LIMIT 1
""")
    suspend fun getUserByUsernameOrEmail(query: String): UserEntity?

    @Dao
    interface FriendRequestDao {
        @Insert
        suspend fun insert(request: FriendRequestEntity)

        @Query("SELECT * FROM friend_requests WHERE toEmail = :email AND status = 'pending'")
        suspend fun getIncomingRequests(email: String): List<FriendRequestEntity>

        @Query("DELETE FROM friend_requests WHERE id = :id")
        suspend fun deleteRequest(id: Int)

        @Query("UPDATE friend_requests SET status = 'accepted' WHERE id = :id")
        suspend fun acceptRequest(id: Int)
    }






    @Transaction
    suspend fun acceptRequest(request: FriendRequestEntity) {
        insert(FriendEntity(userEmail = request.toEmail, friendEmail = request.fromEmail))
        insert(FriendEntity(userEmail = request.fromEmail, friendEmail = request.toEmail))
        deleteRequest(request.id)
    }
}

