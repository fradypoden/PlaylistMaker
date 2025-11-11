package com.example.playlistmaker.media_library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media_library.domain.FavTrInteractor
import com.example.playlistmaker.media_library.domain.FavTrList
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(private val favTrackInteractor: FavTrInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavTrList>()
    fun observeState(): LiveData<FavTrList> = stateLiveData

    private fun processResult(favTracks: List<Track>) {
        when {
            favTracks.isEmpty() -> {
                renderState(
                    FavTrList.Empty(
                        errorMessage = R.string.mediaLibraryEmpty,
                        errorImage = R.drawable.search_error
                    )
                )
            }

            else -> {
                renderState(
                    FavTrList.FavoriteContent(
                        favTracks = favTracks
                    )
                )
            }
        }
    }

    private fun renderState(state: FavTrList) {
        stateLiveData.postValue(state)
    }

    fun getTracks() {
        viewModelScope.launch {
            favTrackInteractor
                .getTracks()
                .collect { favTracks ->
                    processResult(favTracks)
                }
        }
    }
}

