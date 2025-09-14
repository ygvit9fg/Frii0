package com.example.friiomain.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FriendDao {
    @Insert
    suspend fun addFriend(friend: FriendEntity)

    @Query("SELECT * FROM friends WHERE userEmail = :userEmail")
    suspend fun getFriendsForUser(userEmail: String): List<FriendEntity>
}
