package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.usecase.ThemeSwitchInteractor
import com.example.playlistmaker.domain.usecase.ThemeSwitchRepository

class ThemeSwitchInteractorImpl(private val repository: ThemeSwitchRepository) :
    ThemeSwitchInteractor {
    override fun switchTheme(darkThemeEnabled: Boolean) {
        repository.switchTheme(darkThemeEnabled)
    }

    override fun saveTheme(darkThemeEnabled: Boolean) {
        repository.saveTheme(darkThemeEnabled)
    }

    override fun getTheme(darkThemeEnabled: Boolean): Boolean {
        return repository.getTheme(darkThemeEnabled)
    }
}