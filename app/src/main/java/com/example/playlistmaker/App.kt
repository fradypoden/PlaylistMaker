package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.settings.domain.ThemeSwitchInteractor
import com.example.playlistmaker.settings.ui.DARK_THEME_KEY
import com.example.playlistmaker.settings.ui.PRACTICUM_EXAMPLE_PREFERENCES
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    var darkTheme = false
    private val themeSwitch: ThemeSwitchInteractor by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        themeSwitch.switchTheme(sharedPrefs.getBoolean(DARK_THEME_KEY, darkTheme))
    }




}
