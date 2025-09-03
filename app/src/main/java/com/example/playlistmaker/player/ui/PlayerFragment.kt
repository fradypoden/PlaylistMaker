package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentTrackBinding
import com.example.playlistmaker.player.ui.PlayerViewModel.Companion.STATE_PAUSED
import com.example.playlistmaker.player.ui.PlayerViewModel.Companion.STATE_PLAYING
import com.example.playlistmaker.search.domain.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.util.Locale

class PlayerFragment : Fragment() {
    private lateinit var binding: FragmentTrackBinding
    private val viewModel by viewModel<PlayerViewModel>()
    private var mainThreadHandler: Handler? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener { findNavController().navigateUp() }

        mainThreadHandler = Handler(Looper.getMainLooper())
        val track = arguments?.getParcelable<Track>(TRACK)

        binding.trackName.apply { this.text = track?.trackName }
        binding.artistName.apply { this.text = track?.artistName }

        val url = track?.previewUrl

        val viewModel: PlayerViewModel by viewModel { parametersOf(url) }

        binding.play.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.time

        viewModel.updateTimerNow()

        viewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
            when (state.status) {
                STATE_PLAYING -> {
                    binding.play.setImageResource(R.drawable.button_pause)
                    if (state.time != null) {
                        binding.time.text = state.time
                    }
                }
                STATE_PAUSED -> {
                    binding.play.setImageResource(R.drawable.play)
                    if (state.time != null) {
                        binding.time.text = state.time
                    }
                }
            }
        }

        binding.trackTimeMillis.apply {
            this.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)
        }

        binding.artworkUrl100.apply {
            Glide.with(this)
                .load(track?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                .placeholder(R.drawable.placeholder)
                .into(this)
        }

        binding.collectionNameText
        binding.collectionName.apply {
            if (track?.collectionName.isNullOrEmpty()) {
                this.visibility = View.GONE
                binding.collectionNameText.visibility = View.GONE
            } else this.text = track?.collectionName
        }
        binding.releaseDateText
        binding.releaseDate.apply {
            val correctYear = track?.let { yearTransformation(it.releaseDate) }
            if (correctYear.isNullOrEmpty()) {
                this.visibility = View.GONE
                binding.releaseDateText.visibility = View.GONE
            } else this.text = correctYear
        }

        binding.primaryGenreNameText
        binding.primaryGenreName.apply {
            if (track?.primaryGenreName.isNullOrEmpty()) {
                this.visibility = View.GONE
                binding.primaryGenreNameText.visibility = View.GONE
            } else this.text = track?.primaryGenreName
        }

        binding.countryText
        binding.country.apply {
            if (track?.country.isNullOrEmpty()) {
                this.visibility = View.GONE
                binding.countryText.visibility = View.GONE
            } else this.text = track?.country
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

    companion object {
        val TRACK = "track"
    }
}
