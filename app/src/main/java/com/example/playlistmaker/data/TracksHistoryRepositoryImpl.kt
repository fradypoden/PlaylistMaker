package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.usecase.TracksHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TracksHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    TracksHistoryRepository {

    override fun addTrackToHistory(track: Track) {
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

    override fun getTrackHistory(): List<Track> {
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        val json = sharedPreferences.getString(TRACK_LIST, null)
        return Gson().fromJson(json, type)
    }

    override fun clearHistory() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    companion object {
        const val TRACK_LIST = "track_list"
    }
}
