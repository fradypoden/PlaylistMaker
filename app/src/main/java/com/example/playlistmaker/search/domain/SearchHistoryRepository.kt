package com.example.playlistmaker.search.domain


interface SearchHistoryRepository {
    fun saveToHistory(tracks: List<Track>)
    fun getHistory(): List<Track>?
    fun deleteHistory()
}
