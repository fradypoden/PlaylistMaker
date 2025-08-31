package com.example.playlistmaker.settings.domain

interface ThemeSwitchInteractor {
    fun switchTheme(darkThemeEnabled: Boolean)
    fun getTheme(darkThemeEnabled: Boolean) : Boolean
}