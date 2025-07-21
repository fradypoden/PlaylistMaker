package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.usecase.TracksHistoryInteractor
import com.example.playlistmaker.domain.usecase.TracksHistoryRepository

class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository) :
    TracksHistoryInteractor {

    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun getTrackHistory(): List<Track> {
        return repository.getTrackHistory()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}
