package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(val sharingInteractor: SharingInteractor) : ViewModel() {

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val sharingInteractor = Creator.provideSharingInteractor(App.appContext)
                SettingsViewModel(sharingInteractor)
            }
        }
    }

    val themeSwitchInteractor = Creator.provideThemeSwitchInteractor()

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