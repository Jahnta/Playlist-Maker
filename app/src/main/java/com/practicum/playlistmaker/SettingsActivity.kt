package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val shareButton = findViewById<ImageView>(R.id.share_button)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            val message = getString(R.string.share_link)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }

        val supportButton = findViewById<ImageView>(R.id.support_button)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email_to)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, arrayOf(getString(R.string.support_email_subject)))
            supportIntent.putExtra(Intent.EXTRA_TEXT, arrayOf(getString(R.string.support_email_text)))
            startActivity(supportIntent)
        }

        val licenseButton = findViewById<ImageView>(R.id.license_button)
        licenseButton.setOnClickListener {
            val licenseIntent = Intent(Intent.ACTION_VIEW)
            licenseIntent.data = Uri.parse(getString(R.string.license_link))
            startActivity(licenseIntent)
        }

    }

}