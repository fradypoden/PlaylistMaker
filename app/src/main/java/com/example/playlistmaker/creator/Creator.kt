package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
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



    private fun getTracksRepository(context:Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context:Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }



    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient<ArrayList<Track>>(
            context,
            "HISTORY",
            object : TypeToken<ArrayList<Track>>() {}.type)
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }



    private fun getThemeSwitchRepository(sharedPreferences: SharedPreferences): ThemeSwitchRepository {
        return ThemeSwitchRepositoryImpl(sharedPreferences)
    }

    fun provideThemeSwitchInteractor(sharedPreferences: SharedPreferences): ThemeSwitchInteractor {
        return ThemeSwitchInteractorImpl(getThemeSwitchRepository(sharedPreferences))
    }



    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            getExternalNavigator(context),
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
