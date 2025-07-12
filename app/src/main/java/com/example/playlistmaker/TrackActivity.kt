package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.track.Track
import com.google.android.material.appbar.MaterialToolbar
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.util.Locale

class TrackActivity : AppCompatActivity() {

    private var mediaPlayer = MediaPlayer()
    private lateinit var play: ImageView
    private var mainThreadHandler: Handler? = null
    private lateinit var time: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        mainThreadHandler = Handler(Looper.getMainLooper())

        val back = findViewById<MaterialToolbar>(R.id.backButton)
        back.setOnClickListener { finish() }

        val track = intent.getParcelableExtra<Track>("track") as Track

        val trackName = findViewById<TextView>(R.id.trackName).apply { this.text = track.trackName }

        val artistName =
            findViewById<TextView>(R.id.artistName).apply { this.text = track.artistName }

        var url = track.previewUrl
        play = findViewById(R.id.play)
        preparePlayer(url)
        play.setOnClickListener {
            playbackControl()
        }

        time = findViewById(R.id.time)

        val trackTimeMillis = findViewById<TextView>(R.id.trackTimeMillis).apply {
            this.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        }

        val artworkUrl100 = findViewById<ImageView>(R.id.artworkUrl100).apply {
            Glide.with(this)
                .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                .placeholder(R.drawable.placeholder)
                .into(this)
        }

        val collectionNameText = findViewById<TextView>(R.id.collectionNameText)
        val collectionName =
            findViewById<TextView>(R.id.collectionName).apply {
                if (track.collectionName.isNullOrEmpty()) {
                    this.visibility = View.GONE
                    collectionNameText.visibility = View.GONE
                } else this.text = track.collectionName
            }
        val releaseDateText = findViewById<TextView>(R.id.releaseDateText)
        val releaseDate =
            findViewById<TextView>(R.id.releaseDate).apply {
                val correctYear = yearTransformation(track.releaseDate)
                if (correctYear.isNullOrEmpty()) {
                    this.visibility = View.GONE
                    releaseDateText.visibility = View.GONE
                } else this.text = correctYear
            }

        val primaryGenreNameText = findViewById<TextView>(R.id.primaryGenreNameText)
        val primaryGenreName =
            findViewById<TextView>(R.id.primaryGenreName).apply {
                if (track.primaryGenreName.isNullOrEmpty()) {
                    this.visibility = View.GONE
                    primaryGenreNameText.visibility = View.GONE
                } else this.text = track.primaryGenreName
            }

        val countryText = findViewById<TextView>(R.id.countryText)
        val country =
            findViewById<TextView>(R.id.country).apply {
                if (track.country.isNullOrEmpty()) {
                    this.visibility = View.GONE
                    countryText.visibility = View.GONE
                } else this.text = track.country
            }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        play.setImageResource(R.drawable.play)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacks(updateTime)
        mediaPlayer.release()
    }

    private fun yearTransformation(date: String): String? {
        return try {
            val dateTime = OffsetDateTime.parse(date)
            dateTime.year.toString()
        } catch (e: DateTimeParseException) {
            null
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            mainThreadHandler?.removeCallbacks(updateTime)
            time.text = "00:00"
            play.setImageResource(R.drawable.play)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                play.setImageResource(R.drawable.play)
                mainThreadHandler?.removeCallbacks(updateTime)
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                play.setImageResource(R.drawable.button_pause)
                updateTime.run()
            }
        }
    }

    val updateTime = object : Runnable {
        override fun run() {
            val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            time.text = formattedTime
            mainThreadHandler?.postDelayed(this, 500)
        }
    }

}
