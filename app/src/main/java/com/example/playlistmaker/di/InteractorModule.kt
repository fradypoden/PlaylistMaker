package com.example.playlistmaker.di

import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeSwitchInteractor
import com.example.playlistmaker.settings.domain.ThemeSwitchInteractorImpl
import com.example.playlistmaker.sharing.domain.EmailData
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module {

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    factory<ThemeSwitchInteractor> {
        ThemeSwitchInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(
            get(),
            androidContext().getString(R.string.textToShare),
            androidContext().getString(R.string.url),
            EmailData(
                androidContext().getString(R.string.subject),
                androidContext().getString(R.string.textToSupport),
                androidContext().getString(R.string.mail)
            )
        )
    }

}