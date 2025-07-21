package com.example.playlistmaker.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.usecase.ThemeSwitchRepository
import com.example.playlistmaker.presentation.ui.DARK_THEME_KEY

class ThemeSwitchRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    ThemeSwitchRepository {

    override fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    override fun saveTheme(darkThemeEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY, darkThemeEnabled).apply()
    }

    override fun getTheme(darkThemeEnabled: Boolean): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, darkThemeEnabled)
    }
}