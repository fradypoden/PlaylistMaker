package com.example.playlistmaker.track

import android.content.SharedPreferences
import com.example.playlistmaker.TRACK_LIST
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun addTrackToHistory(track: Track) {
        val json = sharedPreferences.getString(TRACK_LIST, null)
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        val currentHistory: ArrayList<Track> = Gson().fromJson(json, type) ?: arrayListOf()

        currentHistory.removeIf { it.trackId == track.trackId }

        currentHistory.add(0, track)

        if (currentHistory.size > 10) {
            currentHistory.subList(10, currentHistory.size).clear()
        }
        val newHistory = Gson().toJson(currentHistory)
        sharedPreferences.edit()
            .putString(TRACK_LIST, newHistory)
            .apply()
    }
}

