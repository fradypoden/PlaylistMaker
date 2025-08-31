package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.TracksState

class SearchActivity : AppCompatActivity() {
    private val track = ArrayList<Track>()
    private var textWatcher: TextWatcher? = null
    private lateinit var adapter: TrackAdapter
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var viewModel: SearchViewModel? = null
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TrackAdapter { OnItemClickListener ->
            if (clickDebounce()) {
                viewModel?.addTrackToHistory(OnItemClickListener)
                val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
                intent.putExtra(TRACK, OnItemClickListener)
                startActivity(intent)
            }
        }
        adapter.tracks = track

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this, SearchViewModel.getFactory())
            .get(SearchViewModel::class.java)

        viewModel?.observeState()?.observe(this) {
            render(it)
        }
        viewModel?.observeTrackHistory()?.observe(this) { trackHistory ->
            if (binding.searchLine.text.isEmpty() && trackHistory.isNotEmpty()) {
                clearScreen()
                binding.recyclerView.visibility = View.VISIBLE
                binding.clearHistoryButton.visibility = View.VISIBLE
                binding.searchHint.visibility = View.VISIBLE
                adapter.tracks.clear()
                adapter.tracks.addAll(trackHistory)
                adapter.notifyDataSetChanged()
            } else if (binding.searchLine.text.isEmpty() && trackHistory.isEmpty()) {
                clearScreen()
            } else {
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

        binding.backButton.setOnClickListener { finish() }

        binding.clearButton.setOnClickListener {
            binding.searchLine.setText("")
            binding.recyclerView.visibility = View.VISIBLE
            binding.searchHint.visibility = View.VISIBLE
            binding.clearHistoryButton.visibility = View.VISIBLE
            viewModel!!.getTrackHistory()
            adapter.notifyDataSetChanged()
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel!!.clearHistory()
            clearScreen()
        }

        if (viewModel!!.getTrackHistory() != null) {
            clearScreen()
            binding.searchHint.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.VISIBLE
            binding.clearHistoryButton.visibility = View.VISIBLE
        } else {
            clearScreen()
        }

        binding.refresh.setOnClickListener {
            val lastQuery = viewModel!!.latestSearchText
            if (lastQuery != null) {
                if (lastQuery.isNotEmpty()) {
                    viewModel!!.searchDebounce(lastQuery)
                }
            }
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel?.searchDebounce(changedText = s?.toString() ?: "")
                if (s.toString().isEmpty() && track.isEmpty()) {
                    clearScreen()
                }
            }
        }
        textWatcher?.let { binding.searchLine.addTextChangedListener(it) }
    }

    private fun clearScreen() {
        binding.refresh.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.errorImage.visibility = View.GONE
        binding.clearButton.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.searchHint.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { binding.searchLine.removeTextChangedListener(it) }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun showLoading() {
        clearScreen()
        binding.apply {
            track.clear()
            progressBar.visibility = View.VISIBLE
        }
    }

    fun showContent(tracksList: List<Track>) {
        clearScreen()
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.visibility = View.VISIBLE
            clearButton.visibility = View.VISIBLE
        }
        adapter.tracks.clear()
        adapter.tracks.addAll(tracksList)
        adapter.notifyDataSetChanged()
    }

    fun showError(errorMessage: String, errorImage: Int) {
        clearScreen()
        binding.apply {
            refresh.visibility = View.VISIBLE
            errorText.visibility = View.VISIBLE
            binding.errorImage.visibility = View.VISIBLE
            clearButton.visibility = View.VISIBLE
            errorText.setText(errorMessage)
            binding.errorImage.setImageResource(errorImage)
        }
    }

    fun showEmpty(errorMessage: String, errorImage: Int) {
        clearScreen()
        binding.clearButton.visibility = View.VISIBLE
        binding.errorText.visibility = View.VISIBLE
        binding.errorImage.visibility = View.VISIBLE
        binding.errorText.setText(errorMessage)
        binding.errorImage.setImageResource(errorImage)
    }

    fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Error -> showError(state.errorMessage, state.errorImage)
            is TracksState.Empty -> showEmpty(state.errorMessage, state.errorImage)
        }
    }

    companion object {
        private const val TRACK = "track"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val HISTORY = "HISTORY"
    }

}
