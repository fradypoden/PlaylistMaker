package com.example.playlistmaker.search.domain

interface SearchHistoryInteractor {

    fun saveToHistory(track: Track)
    fun getHistory() : List<Track>?
    fun deleteHistory()
}