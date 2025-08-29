package com.example.playlistmaker.presentation

import android.app.Application
import com.example.playlistmaker.Creator
import com.example.playlistmaker.presentation.ui.DARK_THEME_KEY
import com.example.playlistmaker.presentation.ui.PRACTICUM_EXAMPLE_PREFERENCES

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        val themeSwitch = Creator.provideThemeSwitchRepository(sharedPrefs)
        themeSwitch.switchTheme(sharedPrefs.getBoolean(DARK_THEME_KEY, darkTheme))
    }
}
