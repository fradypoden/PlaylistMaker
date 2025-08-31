package com.example.playlistmaker.settings.domain

interface ThemeSwitchRepository {
    fun switchTheme(darkThemeEnabled: Boolean)
    fun getTheme(darkThemeEnabled: Boolean) : Boolean
}