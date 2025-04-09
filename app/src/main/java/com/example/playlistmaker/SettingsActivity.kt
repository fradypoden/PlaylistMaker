package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back = findViewById<TextView>(R.id.backButton)
        back.setOnClickListener { finish() }

        val share = findViewById<TextView>(R.id.shareButton)
        share.setOnClickListener { shareApp() }

        val support = findViewById<TextView>(R.id.supportButton)
        support.setOnClickListener { writeToSupport() }

        val userAgreement = findViewById<TextView>(R.id.userAgreementButton)
        userAgreement.setOnClickListener { userAgreement() }
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        val text = "https://practicum.yandex.ru/android-developer/?from=catalog"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.setType("text/plain")
        startActivity(Intent.createChooser(intent, "text"))
    }

    private fun writeToSupport() {
        val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
        val text = "Спасибо разработчикам и разработчицам за крутое приложение!"
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("fradypoden@yandex.ru"))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(intent)
    }

    private fun userAgreement() {
        val url = "https://yandex.ru/legal/practicum_offer/"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

}
