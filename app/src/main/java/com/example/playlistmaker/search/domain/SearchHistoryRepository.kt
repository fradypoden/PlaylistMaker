package com.example.playlistmaker.search.domain


interface SearchHistoryRepository {
    fun saveToHistory(tracks: List<Track>)
    suspend fun getHistory(): List<Track>?
    fun deleteHistory()
}
