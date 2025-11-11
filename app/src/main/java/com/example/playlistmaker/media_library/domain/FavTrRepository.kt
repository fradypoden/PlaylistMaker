package com.example.playlistmaker.media_library.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavTrRepository {
    suspend fun insertTrack(track: Track)
    suspend fun deleteTrack(trackId: Int)
    suspend fun getTracks() : Flow<List<Track>>
}
