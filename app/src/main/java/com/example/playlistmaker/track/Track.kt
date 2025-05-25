package com.example.playlistmaker.track

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)

class TrackResponse(val results: List<Track>)