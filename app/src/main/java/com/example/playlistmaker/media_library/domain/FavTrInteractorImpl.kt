package com.example.playlistmaker.media_library.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavTrInteractorImpl(private val repository: FavTrRepository) : FavTrInteractor {
    override suspend fun insertTrack(track: Track) {
        repository.insertTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        repository.deleteTrack(track)
    }

    override suspend fun getTracks(): Flow<List<Track>> {
        return repository.getTracks().map { tracks ->
            tracks.sortedByDescending { it.dateAdded }
        }
    }
}