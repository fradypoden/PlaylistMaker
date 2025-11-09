package com.example.playlistmaker.di

import android.app.Application.MODE_PRIVATE
import com.example.playlistmaker.PRACTICUM_EXAMPLE_PREFERENCES
import com.example.playlistmaker.player.data.db.TrackDbConvertor
import com.example.playlistmaker.media_library.data.FavTrRepositoryImpl
import com.example.playlistmaker.media_library.domain.FavTrRepository
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.settings.data.ThemeSwitchRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeSwitchRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(),get())
    }

    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(),get())
    }

    factory<ThemeSwitchRepository> {
        ThemeSwitchRepositoryImpl(androidContext().getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE))
    }

    factory { TrackDbConvertor() }

    single<FavTrRepository> {
        FavTrRepositoryImpl(get(), get())
    }

}