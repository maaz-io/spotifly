package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TrackEntity::class, PlaylistEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SpotiflyDatabase : RoomDatabase() {
    abstract fun spotiflyDao(): SpotiflyDao

    companion object {
        @Volatile
        private var INSTANCE: SpotiflyDatabase? = null

        fun getInstance(context: Context): SpotiflyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpotiflyDatabase::class.java,
                    "spotifly_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
