package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.Track

class SearchHistoryRepositoryImpl(private val storage: StorageClient<List<Track>>) :
    SearchHistoryRepository {

    override fun saveToHistory(tracks: List<Track>) {
        storage.storeData(tracks)
    }

    override fun getHistory(): List<Track> {
        val tracks = storage.getData() ?: listOf()
        return tracks
    }

    override fun deleteHistory() {
        storage.deleteData()
    }

}
