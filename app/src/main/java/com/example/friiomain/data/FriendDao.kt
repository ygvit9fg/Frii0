package com.example.friiomain.data

import androidx.room.*

@Dao
interface FriendDao {

    // --- Friends ---
    @Insert
    suspend fun insert(friend: FriendEntity)

    @Query("SELECT * FROM friends WHERE userEmail = :userEmail")
    suspend fun getFriendsForUser(userEmail: String): List<FriendEntity>

    @Query("DELETE FROM friends WHERE userEmail = :userEmail AND friendEmail = :friendEmail")
    suspend fun deleteFriend(userEmail: String, friendEmail: String)

    // --- Requests ---
    @Insert
    suspend fun insertRequest(request: FriendRequestEntity)

    @Query("SELECT * FROM friend_requests WHERE toEmail = :userEmail")
    suspend fun getIncomingRequests(userEmail: String): List<FriendRequestEntity>

    @Query("UPDATE friend_requests SET status = 'accepted' WHERE id = :requestId")
    suspend fun acceptRequest(requestId: Int)


    @Query("DELETE FROM friend_requests WHERE id = :requestId")
    suspend fun deleteRequest(requestId: Int)

    // --- Accept request (транзакционно) ---
    @Transaction
    suspend fun acceptRequest(request: FriendRequestEntity) {
        // Добавляем друга в обе стороны
        insert(FriendEntity(userEmail = request.toEmail, friendEmail = request.fromEmail))
        insert(FriendEntity(userEmail = request.fromEmail, friendEmail = request.toEmail))

        // Удаляем заявку
        deleteRequest(request.id)
    }
}

