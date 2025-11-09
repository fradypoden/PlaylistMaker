package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media_library.domain.FavTrInteractor
import com.example.playlistmaker.player.domain.FavTrState
import com.example.playlistmaker.player.domain.PlayerState
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val mediaPlayer: MediaPlayer,
    private val favTrInteractor: FavTrInteractor
) : ViewModel() {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val DELAY = 300L
    }

    private var timerJob: Job? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val StateLiveData = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = StateLiveData
    private var _isFavorite = MutableLiveData<FavTrState>()
    val isFavorite: LiveData<FavTrState> get() = _isFavorite

    private var currentPosition: Int = 0

    init {
        preparePlayer()
        StateLiveData.value = PlayerState(STATE_DEFAULT)
    }

    override fun onCleared() {
        super.onCleared()
        resetTimer()
        mediaPlayer.release()
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (!track.isFavorite) {
                track.isFavorite = true
                favTrInteractor.insertTrack(track)
                _isFavorite.postValue(FavTrState(true))
            } else {
                track.isFavorite = false
                favTrInteractor.deleteTrack(track)
                _isFavorite.postValue(FavTrState(false))
            }
        }
    }

    fun onPlayButtonClicked() {
        if (StateLiveData.value is PlayerState) {
            when (StateLiveData.value?.status) {
                STATE_PLAYING -> pausePlayer()
                STATE_PREPARED, STATE_PAUSED -> startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            StateLiveData.postValue(PlayerState(STATE_PREPARED))
        }
        mediaPlayer.setOnCompletionListener {
            StateLiveData.postValue(PlayerState(STATE_PREPARED))
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        StateLiveData.postValue(PlayerState(STATE_PLAYING))
        startTimer()
    }

    fun onPause() {
        pausePlayer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        StateLiveData.postValue(PlayerState(STATE_PAUSED))
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                StateLiveData.postValue(
                    PlayerState(
                        status = STATE_PLAYING,
                        time = dateFormat.format(mediaPlayer.currentPosition)
                    )
                )
                delay(DELAY)
            }
        }
    }

    fun updateTimerNow() {
        currentPosition = mediaPlayer.currentPosition
        StateLiveData.postValue(
            PlayerState(
                status = STATE_PAUSED,
                time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
            )
        )
    }

    private fun resetTimer() {
        StateLiveData.postValue(PlayerState(STATE_PAUSED, "00:00"))
        mediaPlayer.seekTo(0)
    }
}
