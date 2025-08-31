package com.example.playlistmaker

import android.app.Application
import android.content.Context
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.ui.DARK_THEME_KEY
import com.example.playlistmaker.settings.ui.PRACTICUM_EXAMPLE_PREFERENCES

class App : Application() {


    companion object {
        lateinit var appContext: Context
    }
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        Creator.init(this)

        appContext = applicationContext
        val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        val themeSwitch = Creator.provideThemeSwitchInteractor()
        themeSwitch.switchTheme(sharedPrefs.getBoolean(DARK_THEME_KEY, darkTheme))
    }




}
