package com.practicum.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.settings.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.model.ThemeSettings
import com.practicum.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val _themeSettings = MutableLiveData<Boolean>()
    val themeSettings: LiveData<Boolean>
        get() = _themeSettings

    init {
        loadThemeSettings()
    }

    private fun loadThemeSettings() {
        _themeSettings.value = settingsInteractor.getThemeSettings().isDarkEnabled
    }

    fun changeThemeSettings(isDarkEnabled: Boolean) {
        settingsInteractor.changeThemeSettings(ThemeSettings(isDarkEnabled))
        _themeSettings.value = isDarkEnabled
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

}