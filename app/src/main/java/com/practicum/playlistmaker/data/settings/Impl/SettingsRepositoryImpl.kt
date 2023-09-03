package com.practicum.playlistmaker.data.settings.Impl

import android.app.Application
import android.content.res.Resources.Theme
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.data.settings.SettingsRepository
import com.practicum.playlistmaker.domain.settings.model.ThemeSettings

class SettingsRepositoryImpl(application: Application) : SettingsRepository {

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val THEME_KEY = "theme_key"
    }

    private val sharedPrefs = application.getSharedPreferences(
        PLAYLIST_MAKER_PREFERENCES,
        AppCompatActivity.MODE_PRIVATE
    )

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            isDarkEnabled = sharedPrefs.getBoolean(THEME_KEY, false)
        )
    }

    override fun changeThemeSettings(themeSettings: ThemeSettings) {
        AppCompatDelegate.setDefaultNightMode(
            if (themeSettings.isDarkEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit().putBoolean(THEME_KEY, themeSettings.isDarkEnabled).apply()
    }

}