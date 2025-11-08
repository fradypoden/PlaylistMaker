package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SearchViewModel>()
    private val track = ArrayList<Track>()
    private lateinit var adapter: TrackAdapter
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isClickAllowed = true
        adapter = TrackAdapter { track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                findNavController().navigate(
                    R.id.action_searchFragment_to_playerFragment,
                    bundleOf(PlayerFragment.TRACK to track)
                )
            }
        }

        adapter.tracks = track

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.recyclerView.adapter = adapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            binding.errorText.visibility = View.GONE
            binding.errorImage.visibility = View.GONE
            render(it)
        }

        viewModel.getTrackHistory()

        binding.clearButton.setOnClickListener {
            binding.searchLine.setText("")
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel?.clearHistory()
            clearScreen()
        }

        binding.refresh.setOnClickListener {
            val lastQuery = viewModel.latestSearchText
            if (lastQuery != null) {
                if (lastQuery.isNotEmpty()) {
                    viewModel?.searchDebounce(lastQuery)
                }
            }
        }

        binding.searchLine.doOnTextChanged { s, _, _, _ ->
            viewModel.searchDebounce(changedText = s?.toString() ?: "")
            binding.clearButton.visibility = View.VISIBLE
            if (s.isNullOrEmpty()) {
                viewModel.getTrackHistory()
            }
        }
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    fun showLoading() {
        clearScreen()
        binding.apply {
            track.clear()
            progressBar.visibility = View.VISIBLE
            clearButton.visibility = View.VISIBLE
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

    fun showError(errorMessage: Int, errorImage: Int) {
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

    fun showEmpty(errorMessage: Int, errorImage: Int) {
        clearScreen()
        binding.clearButton.visibility = View.VISIBLE
        binding.errorText.visibility = View.VISIBLE
        binding.errorImage.visibility = View.VISIBLE
        binding.errorText.setText(errorMessage)
        binding.errorImage.setImageResource(errorImage)
    }

    fun showHistory(tracks: List<Track>?) {
        if (binding.searchLine.text.isEmpty() && tracks!!.isNotEmpty()) {
            clearScreen()
            binding.recyclerView.visibility = View.VISIBLE
            binding.clearHistoryButton.visibility = View.VISIBLE
            binding.searchHint.visibility = View.VISIBLE
            adapter.tracks.clear()
            adapter.tracks.addAll(tracks)
            adapter.notifyDataSetChanged()
        } else if (binding.searchLine.text.isEmpty() && tracks!!.isEmpty()) {
            clearScreen()
        } else {
            binding.searchHint.visibility = View.GONE
            binding.clearHistoryButton.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Error -> showError(state.errorMessage, state.errorImage)
            is TracksState.Empty -> showEmpty(state.errorMessage, state.errorImage)
            is TracksState.HistoryContent -> showHistory(state.tracks)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val HISTORY = "HISTORY"
    }
}
