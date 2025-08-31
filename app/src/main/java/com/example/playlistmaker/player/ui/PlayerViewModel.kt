package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.domain.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val url: String) : ViewModel() {

    companion object {

        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

        fun getFactory(trackUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl)
            }
        }
    }

    private val StateLiveData = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = StateLiveData

    private var currentPosition: Int = 0

    private val mediaPlayer = MediaPlayer()

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = Runnable {
        if (StateLiveData.value?.status == STATE_PLAYING) {
            startTimerUpdate()
        }
    }

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
        startTimerUpdate()
    }

    fun onPause() {
        pausePlayer()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        StateLiveData.postValue(PlayerState(STATE_PAUSED))
    }

    private fun startTimerUpdate() {
        currentPosition = mediaPlayer.currentPosition
        StateLiveData.postValue(PlayerState(status = STATE_PLAYING, time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)))
        handler.postDelayed(timerRunnable, 200)
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

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        StateLiveData.postValue(PlayerState(STATE_PAUSED, "00:00"))
        mediaPlayer.seekTo(0)
    }
}