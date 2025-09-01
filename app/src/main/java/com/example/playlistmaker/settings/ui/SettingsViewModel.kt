package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.ThemeSwitchInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(val sharingInteractor: SharingInteractor, val themeSwitchInteractor: ThemeSwitchInteractor) : ViewModel() {

    fun switchTheme(darkThemeEnabled: Boolean) {
        themeSwitchInteractor.switchTheme(darkThemeEnabled)
    }

    fun getTheme(darkThemeEnabled: Boolean): Boolean {
        return themeSwitchInteractor.getTheme(darkThemeEnabled)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

}