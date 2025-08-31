package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.data.ThemeSwitchRepositoryImpl
import com.example.playlistmaker.search.data.RetrofitNetworkClient
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.PrefsStorageClient
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.settings.domain.ThemeSwitchInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeSwitchInteractor
import com.example.playlistmaker.settings.domain.ThemeSwitchRepository
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.EmailData
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import com.google.gson.reflect.TypeToken

object Creator {



    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(App.appContext))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }



    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient<List<Track>>(
                App.appContext,
            "HISTORY",
            object : TypeToken<ArrayList<Track>>() {}.type)
        )
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }



    private fun getThemeSwitchRepository(): ThemeSwitchRepository {
        return ThemeSwitchRepositoryImpl(App.appContext)
    }

    fun provideThemeSwitchInteractor(): ThemeSwitchInteractor {
        return ThemeSwitchInteractorImpl(getThemeSwitchRepository())
    }



    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(App.appContext)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            getExternalNavigator(),
            shareAppLink = context.getString(R.string.textToShare),
            termsLink = context.getString(R.string.url),
            supportEmailData = EmailData(
                context.getString(R.string.subject),
                context.getString(R.string.textToSupport),
                context.getString(R.string.mail)
            )
        )
    }

}
