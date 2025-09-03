package com.example.playlistmaker.media_library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryFragment : Fragment() {
    private lateinit var binding: FragmentMediaLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewMediaLibrary.adapter = MediaLibraryAdapter(childFragmentManager, lifecycle)

        tabMediator =
            TabLayoutMediator(binding.tabLayout, binding.viewMediaLibrary) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.favoriteTracks)
                    1 -> tab.text = getString(R.string.playlists)
                }
            }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tabMediator.isInitialized) {
            tabMediator.detach()
        }
    }
}
