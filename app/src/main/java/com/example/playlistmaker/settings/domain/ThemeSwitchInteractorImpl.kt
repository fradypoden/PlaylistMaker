package com.example.playlistmaker.settings.domain

class ThemeSwitchInteractorImpl(private val repository: ThemeSwitchRepository) :
    ThemeSwitchInteractor {
    override fun switchTheme(darkThemeEnabled: Boolean) {
        repository.switchTheme(darkThemeEnabled)
    }

    override fun getTheme(darkThemeEnabled: Boolean): Boolean {
        return repository.getTheme(darkThemeEnabled)
    }
}