package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksState

class SearchViewModel(private val context: Context) : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SearchViewModel(app)
            }
        }
    }

    private val tracksInteractor = Creator.provideTracksInteractor(context)
    private val tracksHistoryInteractor = Creator.provideSearchHistoryInteractor(context)

    var latestSearchText: String? = null

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private val trackHistory = MutableLiveData<List<Track>>()
    fun observeTrackHistory(): LiveData<List<Track>> = trackHistory

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
                                        errorMessage = context.getString(R.string.errorConnectionProblems),
                                        errorImage = R.drawable.internet_error
                                    )
                                )
                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    TracksState.Empty(
                                        errorMessage = context.getString(R.string.errorNoResults),
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
        trackHistory.value = tracksHistoryInteractor.getHistory()
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

