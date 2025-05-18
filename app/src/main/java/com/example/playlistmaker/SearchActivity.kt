package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.playlistmaker.track.iTunesSearchAPI
import com.example.playlistmaker.track.Track
import com.example.playlistmaker.track.TrackAdapter
import com.example.playlistmaker.track.TrackResponse
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var value: String? = null
    private lateinit var recyclerView: RecyclerView
    private val iTunesSearchURL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder().baseUrl(iTunesSearchURL)
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val iTunesSearch = retrofit.create(iTunesSearchAPI::class.java)
    private val track = ArrayList<Track>()
    private val adapter = TrackAdapter()
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var refreshButton: Button
    private lateinit var searchLine: EditText

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        errorImage = findViewById(R.id.errorImage)
        errorText = findViewById(R.id.errorText)
        refreshButton = findViewById(R.id.refresh)
        searchLine = findViewById(R.id.searchLine)

        adapter.tracks = track

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

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
            track.clear()
            errorText.visibility = View.INVISIBLE
            errorImage.visibility = View.INVISIBLE
            refreshButton.visibility = View.INVISIBLE
            adapter.notifyDataSetChanged()
            inputMethodManager?.hideSoftInputFromWindow(searchLine.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                value = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        searchLine.addTextChangedListener(simpleTextWatcher)
        if (savedInstanceState != null) {
            value = savedInstanceState.getString("VALUE").toString()
            searchLine.setText(value)
        }
    }

    private fun search(query: String) {
        track.clear()
        adapter.notifyDataSetChanged()
        iTunesSearch.search(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>, response: Response<TrackResponse>
            ) {
                if (response.code() == 200) {
                    track.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        track.addAll(response.body()?.results!!)
                        adapter.notifyDataSetChanged()
                    }
                    if (track.isEmpty()) {
                        error(R.string.errorNoResults, R.drawable.search_error)
                    }
                } else {
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("VALUE", value)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        value = savedInstanceState.getString("VALUE")
    }
}
