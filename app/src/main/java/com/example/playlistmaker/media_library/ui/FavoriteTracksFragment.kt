package com.example.playlistmaker.media_library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.media_library.domain.FavTrList
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private val viewModel by viewModel<FavoriteTracksViewModel>()
    private var _binding: FragmentFavoriteTracksBinding? = null
    private val track = ArrayList<Track>()
    private val binding get() = _binding!!
    private lateinit var adapter: TrackAdapter
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isClickAllowed = true
        viewModel.getTracks()

        viewModel.observeState().observe(viewLifecycleOwner) {
            binding.errorText.visibility = View.GONE
            binding.errorImage.visibility = View.GONE
            render(it)
        }

        adapter = TrackAdapter { track ->
            if (clickDebounce()) {
                lifecycleScope.launch {
                    findNavController().navigate(
                        R.id.action_mediaLibraryFragment_to_playerFragment,
                        bundleOf(PlayerFragment.TRACK to track)
                    )
                }
            }
        }

        adapter.tracks = track
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }

    fun showEmpty(errorMessage: Int, errorImage: Int) {
        binding.recyclerView.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.errorImage.visibility = View.VISIBLE
        binding.errorText.setText(errorMessage)
        binding.errorImage.setImageResource(errorImage)
    }

    fun showContent(tracksList: List<Track>) {
        binding.apply {
            errorText.visibility = View.GONE
            errorImage.visibility = View.GONE
            recyclerView.adapter = adapter
            recyclerView.visibility = View.VISIBLE
        }
        binding.recyclerView.visibility = View.VISIBLE
        adapter.tracks.clear()
        adapter.tracks.addAll(tracksList)
        adapter.notifyDataSetChanged()
    }

    fun render(state: FavTrList) {
        when (state) {
            is FavTrList.FavoriteContent -> showContent(state.favTracks)
            is FavTrList.Empty -> showEmpty(state.errorMessage, state.errorImage)
        }
    }
}
