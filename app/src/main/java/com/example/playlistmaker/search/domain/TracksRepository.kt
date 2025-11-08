package com.example.playlistmaker.search.domain

import com.example.playlistmaker.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}
