package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksState

class SearchViewModel: ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel()
            }
        }
    }

    private val tracksInteractor = Creator.provideTracksInteractor()
    private val tracksHistoryInteractor = Creator.provideSearchHistoryInteractor()

    var latestSearchText: String? = null

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    fun searchDebounce(changedText: String) {
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(
                TracksState.Loading
            )

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {
                        val tracks = mutableListOf<Track>()
                        if (foundTracks != null) {
                            tracks.addAll(foundTracks)
                        }
                        when {
                            errorMessage != null -> {
                                renderState(
                                    TracksState.Error(
                                        errorMessage = R.string.errorConnectionProblems,
                                        errorImage = R.drawable.internet_error
                                    )
                                )
                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    TracksState.Empty(
                                        errorMessage = R.string.errorNoResults,
                                        errorImage = R.drawable.search_error
                                    )
                                )
                            }

                            else -> {
                                renderState(
                                    TracksState.Content(
                                        tracks = tracks,
                                    )
                                )
                            }
                        }
                    }
                }
            })
        }
    }

    fun addTrackToHistory(track: Track) {
        tracksHistoryInteractor.saveToHistory(track)
        getTrackHistory()
    }

    fun getTrackHistory() {
        renderState(TracksState.HistoryContent(tracksHistoryInteractor.getHistory()))
        tracksHistoryInteractor.getHistory()
    }

    fun clearHistory() {
        tracksHistoryInteractor.deleteHistory()
        getTrackHistory()
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}

