package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.track.OnItemClickListener
import com.example.playlistmaker.track.SearchHistory
import com.example.playlistmaker.track.Track
import com.example.playlistmaker.track.TrackAdapter
import com.example.playlistmaker.track.TrackResponse
import com.example.playlistmaker.track.iTunesSearch
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val HISTORY_SEARCH = "history_search"
const val TRACK_LIST = "track_list"

class SearchActivity : AppCompatActivity() {
    private var value: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    private val track = ArrayList<Track>()
    lateinit var adapter: TrackAdapter
    lateinit var historyAdapter: TrackAdapter
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var refreshButton: Button
    private lateinit var searchLine: EditText
    private lateinit var hintMessage: TextView
    private lateinit var clearHistoryButton: Button

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val sharedPreferences = getSharedPreferences(HISTORY_SEARCH, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPreferences)

        errorImage = findViewById(R.id.errorImage)
        errorText = findViewById(R.id.errorText)
        refreshButton = findViewById(R.id.refresh)
        searchLine = findViewById(R.id.searchLine)

        hintMessage = findViewById(R.id.searchHint)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        val type = object : TypeToken<ArrayList<Track>>() {}.type

        val onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(item: Track) {
                searchHistory.addTrackToHistory(item)
                historyAdapter.notifyDataSetChanged()
                adapter.notifyDataSetChanged()
                val trackIntent = Intent(this@SearchActivity, TrackActivity::class.java)
                trackIntent.putExtra("track", item)
                startActivity(trackIntent)
            }
        }

        adapter = TrackAdapter(track, onItemClickListener)
        adapter.tracks = track
        historyAdapter = TrackAdapter(track, onItemClickListener)
        historyAdapter.tracks = track

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == TRACK_LIST) {
                val json = sharedPreferences?.getString(TRACK_LIST, null)
                if (json != null) {
                    if (recyclerView.adapter != adapter) {
                        historyAdapter.tracks.clear()
                        val trackListType = object : TypeToken<ArrayList<Track>>() {}.type
                        val tracks: List<Track> = Gson().fromJson(json, trackListType)
                        historyAdapter.tracks.addAll(tracks)
                        historyAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        val back = findViewById<MaterialToolbar>(R.id.backButton)
        back.setOnClickListener { finish() }

        searchLine.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchLine.text.isNotEmpty()) {
                    search(searchLine.text.toString())
                }
            }
            false
        }

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        val clearButton = findViewById<ImageView>(R.id.clearButton)
        clearButton.setOnClickListener {
            searchLine.setText("")
            clearScreen()
            inputMethodManager?.hideSoftInputFromWindow(searchLine.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                value = s.toString()
                if (searchLine.hasFocus() && s?.isEmpty() == true) {
                    clearScreen()
                    val json = sharedPreferences.getString(TRACK_LIST, null)
                    if (json != null) {
                        recyclerView.adapter = historyAdapter
                        historyAdapter.notifyDataSetChanged()
                        historyAdapter.tracks = Gson().fromJson(json, type)
                        hintMessage.visibility = View.VISIBLE
                        recyclerView.visibility = View.VISIBLE
                        clearHistoryButton.visibility = View.VISIBLE
                    }
                } else {
                    hintMessage.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        searchLine.addTextChangedListener(simpleTextWatcher)
        if (savedInstanceState != null) {
            value = savedInstanceState.getString("VALUE").toString()
            searchLine.setText(value)
        }

        clearHistoryButton.setOnClickListener {
            sharedPreferences.edit()
                .clear()
                .apply()
            hintMessage.visibility = View.GONE
            recyclerView.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }

        val json = sharedPreferences.getString(TRACK_LIST, null)
        if (json != null) {
            recyclerView.adapter = historyAdapter
            clearScreen()
            historyAdapter.tracks = Gson().fromJson(json, type)
            historyAdapter.notifyDataSetChanged()
        } else {
            hintMessage.visibility = View.GONE
            recyclerView.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }
    }

    private fun search(query: String) {
        clearScreen()
        iTunesSearch.search(query).enqueue(object : Callback<TrackResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<TrackResponse>, response: Response<TrackResponse>
            ) {
                if (response.code() == 200) {
                    recyclerView.adapter = adapter
                    track.clear()
                    clearScreen()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        Log.i("NetworkResponse", "Ответ: ${response.code()}")
                        track.addAll(response.body()?.results!!)
                        recyclerView.visibility = View.VISIBLE
                        adapter.notifyDataSetChanged()
                    }
                    if (track.isEmpty()) {
                        Log.i("NetworkResponse", "Ответ: ${response.code()}")
                        error(R.string.errorNoResults, R.drawable.search_error)
                    }
                } else {
                    Log.i("NetworkResponse", "Ответ: ${response.code()}")
                    refreshButton.visibility = View.VISIBLE
                    error(R.string.errorConnectionProblems, R.drawable.internet_error)
                    refreshButton.setOnClickListener {
                        search(searchLine.text.toString())
                    }
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                refreshButton.visibility = View.VISIBLE
                error(R.string.errorConnectionProblems, R.drawable.internet_error)
                refreshButton.setOnClickListener {
                    search(searchLine.text.toString())
                }
            }

            private fun error(errorMessageID: Int, errorImageID: Int) {
                errorText.visibility = View.VISIBLE
                errorImage.visibility = View.VISIBLE
                errorText.setText(errorMessageID)
                errorImage.setImageResource(errorImageID)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearScreen() {
        refreshButton.visibility = View.GONE
        errorText.visibility = View.GONE
        errorImage.visibility = View.GONE
        track.clear()
        adapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("VALUE", value)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        value = savedInstanceState.getString("VALUE")
    }
}
