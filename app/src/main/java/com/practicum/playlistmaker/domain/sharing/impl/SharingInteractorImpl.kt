package com.practicum.playlistmaker.domain.sharing.impl

import android.content.Context
import com.practicum.playlistmaker.data.sharing.ContentProvider
import com.practicum.playlistmaker.data.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val contentProvider: ContentProvider
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink() : String {
        return contentProvider.getShareAppLink()
    }

    private fun getTermsLink() : String {
        return contentProvider.getTermsLink()
    }

    private fun getSupportEmailData() : EmailData {
        return contentProvider.getSupportEmailData()
    }
}