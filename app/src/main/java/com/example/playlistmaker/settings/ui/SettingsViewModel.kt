package com.example.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator

class SettingsViewModel(private val context: Context) : ViewModel() {

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SettingsViewModel(app)
            }
        }
    }

    val sharingInteractor = Creator.provideSharingInteractor(context)
    val themeSwitchInteractor = Creator.provideThemeSwitchInteractor(context)

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