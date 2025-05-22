package com.example.playlistmaker.track

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val iTunesSearchURL = "https://itunes.apple.com"
val retrofit = Retrofit.Builder().baseUrl(iTunesSearchURL)
    .addConverterFactory(GsonConverterFactory.create()).build()
val iTunesSearch = retrofit.create(iTunesSearchAPI::class.java)