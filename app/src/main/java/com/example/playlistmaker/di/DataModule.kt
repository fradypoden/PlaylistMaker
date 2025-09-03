package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.PrefsStorageClient
import com.example.playlistmaker.search.data.RetrofitNetworkClient
import com.example.playlistmaker.search.data.StorageClient
import com.example.playlistmaker.search.data.iTunesApiService
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.SearchFragment.Companion.HISTORY
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<iTunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApiService::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(HISTORY, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<StorageClient<List<Track>>> {
        PrefsStorageClient(HISTORY, get(), get(), object : TypeToken<ArrayList<Track>>() {}.type)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single<ExternalNavigator>{
        ExternalNavigatorImpl(androidContext())
    }
}
