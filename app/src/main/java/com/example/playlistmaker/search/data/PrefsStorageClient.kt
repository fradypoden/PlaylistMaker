package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.google.gson.Gson
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    private val dataKey: String,
    private val gson: Gson,
    private val prefs: SharedPreferences,
    private val type: Type
) : StorageClient<T> {

    override fun storeData(data: T) {
        prefs.edit().putString(dataKey, gson.toJson(data, type)).apply()
    }

    override fun getData(): T? {
        val dataJson = prefs.getString(dataKey, null)
        if (dataJson == null) {
            return null
        } else {
            return gson.fromJson(dataJson, type)
        }
    }

    override fun deleteData() {
        prefs.edit()
            .clear()
            .apply()
    }
}