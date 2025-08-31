package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityTrackBinding
import com.example.playlistmaker.search.domain.Track
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var mainThreadHandler: Handler? = null
    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: ActivityTrackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainThreadHandler = Handler(Looper.getMainLooper())

        binding.backButton.setOnClickListener { finish() }

        val track = intent.getParcelableExtra<Track>("track") as Track

        binding.trackName.apply { this.text = track.trackName }
        binding.artistName.apply { this.text = track.artistName }

        var url = track.previewUrl
        binding.play.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        viewModel = ViewModelProvider(this, PlayerViewModel.getFactory(url))
            .get(PlayerViewModel::class.java)

        viewModel.observeProgressTime().observe(this) {
            binding.time.text = it
        }

        viewModel.observePlayerState().observe(this) { state ->
            if (state == PlayerViewModel.STATE_PLAYING) {
                binding.play.setImageResource(R.drawable.button_pause)
            } else {
                binding.play.setImageResource(R.drawable.play)
            }
        }

        binding.time

        binding.trackTimeMillis.apply {
            this.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        }

        binding.artworkUrl100.apply {
            Glide.with(this)
                .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                .placeholder(R.drawable.placeholder)
                .into(this)
        }

        binding.collectionNameText
        binding.collectionName.apply {
                if (track.collectionName.isNullOrEmpty()) {
                    this.visibility = View.GONE
                    binding.collectionNameText.visibility = View.GONE
                } else this.text = track.collectionName
            }
        binding.releaseDateText
        binding.releaseDate.apply {
                val correctYear = yearTransformation(track.releaseDate)
                if (correctYear.isNullOrEmpty()) {
                    this.visibility = View.GONE
                    binding.releaseDateText.visibility = View.GONE
                } else this.text = correctYear
            }

        binding.primaryGenreNameText
        binding.primaryGenreName.apply {
                if (track.primaryGenreName.isNullOrEmpty()) {
                    this.visibility = View.GONE
                    binding.primaryGenreNameText.visibility = View.GONE
                } else this.text = track.primaryGenreName
            }

        binding.countryText
        binding.country.apply {
                if (track.country.isNullOrEmpty()) {
                    this.visibility = View.GONE
                    binding.countryText.visibility = View.GONE
                } else this.text = track.country
            }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun yearTransformation(date: String): String? {
        return try {
            val dateTime = OffsetDateTime.parse(date)
            dateTime.year.toString()
        } catch (e: DateTimeParseException) {
            null
        }
    }
}
