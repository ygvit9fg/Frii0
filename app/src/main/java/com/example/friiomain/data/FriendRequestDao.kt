package com.example.friiomain.data

import androidx.room.*

@Dao
interface FriendRequestDao {

    @Insert
    suspend fun insert(request: FriendRequestEntity)

    @Query("SELECT * FROM friend_requests WHERE toEmail = :userEmail")
    suspend fun getIncomingRequests(userEmail: String): List<FriendRequestEntity>

    @Query("UPDATE friend_requests SET status = 'accepted' WHERE id = :requestId")
    suspend fun acceptRequest(requestId: Int)

    @Query("DELETE FROM friend_requests WHERE id = :requestId")
    suspend fun deleteRequest(requestId: Int)
}
