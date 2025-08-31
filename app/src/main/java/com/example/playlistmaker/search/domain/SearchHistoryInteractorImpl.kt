package com.example.playlistmaker.search.domain

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun saveToHistory(track: Track) {
        repository.saveToHistory(track)
    }

    override fun getHistory() : List<Track>? {
        return repository.getHistory()
    }

    override fun deleteHistory() {
        repository.deleteHistory()
    }
}