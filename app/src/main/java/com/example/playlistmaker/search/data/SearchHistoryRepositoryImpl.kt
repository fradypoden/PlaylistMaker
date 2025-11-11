package com.example.playlistmaker.search.data

import com.example.playlistmaker.player.data.db.AppDatabase
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchHistoryRepositoryImpl(private val storage: StorageClient<List<Track>>, private val appDatabase: AppDatabase) :
    SearchHistoryRepository {

    override fun saveToHistory(tracks: List<Track>) {
        storage.storeData(tracks)
    }

    override suspend fun getHistory(): List<Track> {
        val tracks = storage.getData() ?: listOf()
        val favTrack =
            withContext(Dispatchers.IO) { appDatabase.trackDao().getTracksById() }
        val updatedTracks = tracks.map {
            it.copy(isFavorite = it.trackId in favTrack)
        }
        return updatedTracks
    }

    override fun deleteHistory() {
        storage.deleteData()
    }

}
