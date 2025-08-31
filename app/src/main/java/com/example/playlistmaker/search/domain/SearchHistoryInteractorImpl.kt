package com.example.playlistmaker.search.domain

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun saveToHistory(track: Track) {
        val tracks = repository.getHistory()?.toMutableList()
        if (tracks != null) {
            tracks.removeIf { it.trackId == track.trackId }
            tracks.add(0, track)
            if (tracks.size > 10) {
                tracks.subList(10, tracks.size).clear()
            }
            repository.saveToHistory(tracks)
        }
    }

    override fun getHistory(): List<Track>? {
        return repository.getHistory()
    }

    override fun deleteHistory() {
        repository.deleteHistory()
    }
}