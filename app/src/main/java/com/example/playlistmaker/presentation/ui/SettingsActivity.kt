package com.example.playlistmaker.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar


const val PRACTICUM_EXAMPLE_PREFERENCES = "practicum_example_preferences"
const val DARK_THEME_KEY = "key_for_dark_theme"


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back = findViewById<MaterialToolbar>(R.id.backButton)
        back.setOnClickListener { finish() }

        val share = findViewById<TextView>(R.id.shareButton)
        share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            val text = getString(R.string.textToShare)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.setType("text/plain")
            startActivity(Intent.createChooser(intent, "Тайтл не меняется"))
        }

        val support = findViewById<TextView>(R.id.supportButton)
        support.setOnClickListener {
            val subject = getString(R.string.subject)
            val text = getString(R.string.textToSupport)
            val mail = getString(R.string.mail)
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(intent)
        }

        val userAgreement = findViewById<TextView>(R.id.userAgreementButton)
        userAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val url = getString(R.string.url)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        val themeSwitch = Creator.provideThemeSwitchRepository(sharedPrefs)
        val themeSwitcher = findViewById<Switch>(R.id.themeSwitcher)
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            themeSwitch.switchTheme(checked)

            themeSwitcher.setOnClickListener {
                themeSwitch.saveTheme(checked)
            }
        }

        val darkTheme = false
        themeSwitcher.isChecked = themeSwitch.getTheme(darkTheme)
    }
}
