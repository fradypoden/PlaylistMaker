package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.ui.DARK_THEME_KEY
import com.example.playlistmaker.settings.ui.PRACTICUM_EXAMPLE_PREFERENCES

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        val themeSwitch = Creator.provideThemeSwitchInteractor(this)
        themeSwitch.switchTheme(sharedPrefs.getBoolean(DARK_THEME_KEY, darkTheme))
    }
}
