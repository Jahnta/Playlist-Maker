package com.practicum.playlistmaker.data.sharing.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.sharing.ContentProvider
import com.practicum.playlistmaker.domain.sharing.model.EmailData

class ContentProviderImpl(private val context: Context) : ContentProvider {
    override fun getShareAppLink(): String {
        return context.getString(R.string.share_link)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.license_link)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.support_email_to),
            subject = context.getString(R.string.support_email_subject),
            text = context.getString(R.string.support_email_text)
        )
    }
}