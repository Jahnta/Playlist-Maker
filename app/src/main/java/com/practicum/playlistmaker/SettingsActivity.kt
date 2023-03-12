package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var shareButton: ImageView
    private lateinit var supportButton: ImageView
    private lateinit var licenseButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        backButton = findViewById(R.id.back)
        themeSwitcher = findViewById(R.id.themeSwitcher)
        shareButton = findViewById(R.id.share_button)
        supportButton = findViewById(R.id.support_button)
        licenseButton = findViewById(R.id.license_button)

        backButton.setOnClickListener {
            finish()
        }

        themeSwitcher.isChecked = sharedPrefs.getBoolean(THEMESWITCHER_KEY, false)
        themeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            sharedPrefs.edit().putBoolean(THEMESWITCHER_KEY, isChecked).apply()
        }


        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            val message = getString(R.string.share_link)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }

        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(getString(R.string.support_email_to))
            )
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_text))
            startActivity(supportIntent)
        }

        licenseButton.setOnClickListener {
            val licenseIntent = Intent(Intent.ACTION_VIEW)
            licenseIntent.data = Uri.parse(getString(R.string.license_link))
            startActivity(licenseIntent)
        }

    }

}