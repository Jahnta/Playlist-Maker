package com.practicum.playlistmaker

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
            val message = "https://practicum.yandex.ru/android-developer/"
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }

        val supportButton = findViewById<ImageView>(R.id.support_button)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("dabrusov@yandex.ru"))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, arrayOf("Сообщение разработчикам и разработчицам приложения Playlist Maker"))
            supportIntent.putExtra(Intent.EXTRA_TEXT, arrayOf("Спасибо разработчикам и разработчицам за крутое приложение!"))
            startActivity(supportIntent)
        }

        val licenseButton = findViewById<ImageView>(R.id.license_button)
        licenseButton.setOnClickListener {
            val licenseIntent = Intent(Intent.ACTION_VIEW)
            licenseIntent.data = Uri.parse("https://yandex.ru/legal/practicum_offer/")
            startActivity(licenseIntent)
        }

    }

}