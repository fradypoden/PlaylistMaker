package com.example.playlistmaker.track

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    var tracks: ArrayList<Track>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { onItemClickListener.onItemClick(tracks[position]) }
    }
}

interface OnItemClickListener {
    fun onItemClick(item: Track)
}
