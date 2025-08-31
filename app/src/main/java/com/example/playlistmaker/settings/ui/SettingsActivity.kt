package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding

const val PRACTICUM_EXAMPLE_PREFERENCES = "practicum_example_preferences"
const val DARK_THEME_KEY = "key_for_dark_theme"

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        val themeSwitchInteractor = Creator.provideThemeSwitchInteractor(sharedPrefs)
        val sharingInteractor = Creator.provideSharingInteractor(this)

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory(sharingInteractor, themeSwitchInteractor)).get(
            SettingsViewModel::class.java
        )

        binding.backButton.setOnClickListener { finish() }

        binding.shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        binding.supportButton.setOnClickListener {
            viewModel.openSupport()
        }

        binding.userAgreementButton.setOnClickListener {
            viewModel.openTerms()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchTheme(checked)
        }

        val darkTheme = false
        binding.themeSwitcher.isChecked = viewModel.getTheme(darkTheme)

    }

}
