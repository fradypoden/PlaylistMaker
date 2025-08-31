package com.example.playlistmaker.search.domain

import com.example.playlistmaker.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}
