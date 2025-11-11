package com.example.playlistmaker.media_library.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val playlistName: String,
    val playlistDescription: String,
    val path: String,
    val tracksId: String = (Gson().toJson(emptyList<String>())),
    val trackCount: Int = 0
)