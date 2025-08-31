package com.example.playlistmaker.search.domain

sealed interface TracksState {

    object Loading : TracksState

    data class Content(
        val tracks: List<Track>
    ) : TracksState

    data class Error(
        val errorMessage: Int, val errorImage: Int
    ) : TracksState

    data class Empty(
        val errorMessage: Int, val errorImage: Int
    ) : TracksState

    data class HistoryContent(
        val tracks: List<Track>?
    ) : TracksState

}
