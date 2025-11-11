package com.example.playlistmaker.media_library.domain

data class Playlist(
    val id: Int,
    val playlistName: String,
    val playlistDescription: String,
    val path: String,
    val tracksId: String,
    val trackCount: Int
)