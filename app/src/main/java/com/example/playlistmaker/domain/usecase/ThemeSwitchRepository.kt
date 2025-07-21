package com.example.playlistmaker.domain.usecase

interface ThemeSwitchRepository {
    fun switchTheme(darkThemeEnabled: Boolean)
    fun saveTheme(darkThemeEnabled: Boolean)
    fun getTheme(darkThemeEnabled: Boolean) : Boolean
}