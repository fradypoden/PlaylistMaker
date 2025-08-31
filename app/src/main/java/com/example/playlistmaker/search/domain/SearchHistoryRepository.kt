package com.example.playlistmaker.search.domain


interface SearchHistoryRepository {
    fun saveToHistory(track: Track)
    fun getHistory(): List<Track>?
    fun deleteHistory()
}
