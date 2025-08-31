package com.example.playlistmaker.search.domain

sealed interface TracksState {

    object Loading : TracksState

    data class Content(
        val tracks: List<Track>
    ) : TracksState

    data class Error(
        val errorMessage: String, val errorImage: Int
    ) : TracksState

    data class Empty(
        val errorMessage: String, val errorImage: Int
    ) : TracksState

}
