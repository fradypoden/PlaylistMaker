package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.data.ThemeSwitchRepositoryImpl
import com.example.playlistmaker.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.ThemeSwitchInteractorImpl
import com.example.playlistmaker.domain.impl.TracksHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.usecase.ThemeSwitchInteractor
import com.example.playlistmaker.domain.usecase.ThemeSwitchRepository
import com.example.playlistmaker.domain.usecase.TracksHistoryInteractor
import com.example.playlistmaker.domain.usecase.TracksHistoryRepository

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getTracksHistoryRepository(sharedPreferences: SharedPreferences): TracksHistoryRepository{
        return TracksHistoryRepositoryImpl(sharedPreferences)
    }

    fun provideTracksHistoryRepository(sharedPreferences: SharedPreferences): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository(sharedPreferences))
    }

    private fun getThemeSwitchRepository(sharedPreferences: SharedPreferences): ThemeSwitchRepository {
        return ThemeSwitchRepositoryImpl(sharedPreferences)
    }

    fun provideThemeSwitchRepository(sharedPreferences: SharedPreferences): ThemeSwitchInteractor {
        return ThemeSwitchInteractorImpl(getThemeSwitchRepository(sharedPreferences))
    }

}
