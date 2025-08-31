package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.Track

class SearchHistoryRepositoryImpl(private val storage: StorageClient<ArrayList<Track>>) :
    SearchHistoryRepository {

    override fun saveToHistory(track: Track) {
        val tracks = storage.getData() ?: arrayListOf()
        tracks.removeIf { it.trackId == track.trackId }
        tracks.add(0, track)
        if (tracks.size > 10) {
            tracks.subList(10, tracks.size).clear()
        }
        storage.storeData(tracks)
    }

    override fun getHistory(): List<Track>? {
        val tracks = storage.getData() ?: listOf()
        return tracks
    }

    override fun deleteHistory() {
        storage.deleteData()
    }

}
