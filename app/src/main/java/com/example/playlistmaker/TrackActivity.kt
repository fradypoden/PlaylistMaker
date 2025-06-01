package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        val back = findViewById<MaterialToolbar>(R.id.backButton)
        back.setOnClickListener { finish() }

        val track = intent.getParcelableExtra<Track>("track") as Track

        val trackName = findViewById<TextView>(R.id.trackName).apply { this.text = track.trackName }
        val artistName =
            findViewById<TextView>(R.id.artistName).apply { this.text = track.artistName }
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

    private fun yearTransformation(date: String): String? {
        return try {
            val dateTime = OffsetDateTime.parse(date)
            dateTime.year.toString()
        } catch (e: DateTimeParseException) {
            null
        }
    }
}
