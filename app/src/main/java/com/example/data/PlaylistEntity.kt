package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverType: String = "gradient", // "gradient", "synthwave", "lofi", "liked"
    val isCustom: Boolean = true,
    val trackIdsCsv: String = "", // Comma-separated track IDs: "1,2,3"
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getTrackIds(): List<Long> {
        if (trackIdsCsv.isBlank()) return emptyList()
        return trackIdsCsv.split(",").mapNotNull { it.trim().toLongOrNull() }
    }
}
