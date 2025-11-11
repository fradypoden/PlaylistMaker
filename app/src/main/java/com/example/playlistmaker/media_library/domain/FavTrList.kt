package com.example.playlistmaker.media_library.domain

import com.example.playlistmaker.search.domain.Track

sealed interface FavTrList {

    data class FavoriteContent(
        val favTracks: List<Track>
    ) : FavTrList

    data class Empty(
        val errorMessage: Int, val errorImage: Int
    ) : FavTrList


}