package com.example.playlistmaker.sharing.domain

interface ExternalNavigator {
    fun shareApp(shareAppLink: String)
    fun openTerms(termsLink: String)
    fun openSupport(supportEmailData: EmailData)
}