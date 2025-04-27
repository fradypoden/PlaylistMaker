package com.example.playlistmaker.track

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R


class TrackViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {

    val trackName: TextView
    val artistName: TextView
    val trackTime: TextView
    val artworkUrl100: ImageView

    init {
        trackName = parentView.findViewById(R.id.trackName)
        artistName = parentView.findViewById(R.id.artistName)
        trackTime = parentView.findViewById(R.id.trackTime)
        artworkUrl100 = parentView.findViewById(R.id.artworkUrl100)
    }

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .centerCrop()
            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
            .placeholder(R.drawable.placeholder)
            .into(artworkUrl100)
    }

    companion object {
        fun create(parent: ViewGroup): TrackViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
            return TrackViewHolder(view)
        }
    }
}