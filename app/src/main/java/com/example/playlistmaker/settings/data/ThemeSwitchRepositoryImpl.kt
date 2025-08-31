package com.example.playlistmaker.settings.data

import android.app.Application.MODE_PRIVATE
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.ThemeSwitchRepository
import com.example.playlistmaker.settings.ui.DARK_THEME_KEY
import com.example.playlistmaker.settings.ui.PRACTICUM_EXAMPLE_PREFERENCES

class ThemeSwitchRepositoryImpl(private val context: Context) :
    ThemeSwitchRepository {
    private val sharedPreferences =
        context.getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)

    override fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY, darkThemeEnabled).apply()
    }

    override fun getTheme(darkThemeEnabled: Boolean): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, darkThemeEnabled)
    }
}