package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.settings.domain.ThemeSwitchInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themeSwitchInteractor: ThemeSwitchInteractor
) : ViewModel() {

    companion object {
        fun getFactory(
            sharingInteractor: SharingInteractor,
            themeSwitchInteractor: ThemeSwitchInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    sharingInteractor,
                    themeSwitchInteractor
                )
            }
        }
    }
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