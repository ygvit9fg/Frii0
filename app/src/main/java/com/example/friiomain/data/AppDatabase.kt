package com.example.friiomain.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        NotificationEntity::class,
        FriendEntity::class,
        FriendRequestEntity::class // 👈 добавили
    ],
    version = 4, // обязательно увеличь версию
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun friendDao(): FriendDao
    abstract fun friendRequestDao(): FriendRequestDao // 👈 новый DAO
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "friio_database"
                )
                    .fallbackToDestructiveMigration() // сброс базы при изменении схемы
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
