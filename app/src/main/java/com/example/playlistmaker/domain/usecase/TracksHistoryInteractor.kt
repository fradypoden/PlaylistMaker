package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.models.Track

interface TracksHistoryInteractor {
    fun addTrackToHistory(track: Track)
    fun getTrackHistory(): List<Track>
    fun clearHistory()
}
