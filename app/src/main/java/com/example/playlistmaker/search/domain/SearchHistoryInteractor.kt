package com.example.playlistmaker.search.domain

interface SearchHistoryInteractor {

    suspend fun saveToHistory(track: Track)
    suspend fun getHistory() : List<Track>?
    fun deleteHistory()
}