package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val url: String, private val mediaPlayer: MediaPlayer) : ViewModel() {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

    private var timerJob: Job? = null

    private val StateLiveData = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = StateLiveData

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

    fun onPlayButtonClicked() {
        if (StateLiveData.value is PlayerState) {
            when (StateLiveData.value?.status) {
                STATE_PLAYING -> pausePlayer()
                STATE_PREPARED, STATE_PAUSED -> startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
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
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                StateLiveData.postValue(PlayerState(status = STATE_PLAYING, time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)))
                delay(300L)
            }
        }
    }

    fun updateTimerNow(){
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
