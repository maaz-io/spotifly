package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String,
    val album: String,
    val durationSeconds: Int,
    val coverDrawable: String, // e.g. "synthwave", "lofi", "app_icon", or color hex
    val audioGenre: String = "synthwave", // Used for procedural melodic synthesizer
    val bpm: Int = 120,
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = true,
    val playCount: Int = 0,
    val source: String = "local", // "local" or "ingested_ytdlp"
    val filePath: String = "~/Music/Spotifly/Library/",
    val createdAt: Long = System.currentTimeMillis()
)
