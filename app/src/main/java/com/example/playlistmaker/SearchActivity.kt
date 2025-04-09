package com.example.playlistmaker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val back = findViewById<TextView>(R.id.backButton)
        back.setOnClickListener { finish() }

    }


}
