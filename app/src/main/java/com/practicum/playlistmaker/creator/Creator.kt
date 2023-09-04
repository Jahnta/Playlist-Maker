package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.practicum.playlistmaker.data.player.PlayerRepository
import com.practicum.playlistmaker.data.player.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.search.SearchRepository
import com.practicum.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.data.search.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.settings.Impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.settings.SettingsRepository
import com.practicum.playlistmaker.data.sharing.ContentProvider
import com.practicum.playlistmaker.data.sharing.ExternalNavigator
import com.practicum.playlistmaker.data.sharing.impl.ContentProviderImpl
import com.practicum.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.search.SearchInteractor
import com.practicum.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.domain.settings.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.impl.SharingInteractorImpl

object Creator {

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            externalNavigator = provideExternalNavigator(context),
            contentProvider = provideContentProvider(context)
        )
    }

    private fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    private fun provideContentProvider(context: Context): ContentProvider {
        return ContentProviderImpl(context)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(
            settingsRepository = provideSettingRepository(context)
        )
    }

    private fun provideSettingRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context.applicationContext as Application)
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(
            provideSearchRepository(context)
        )
    }

    private fun provideSearchRepository(context: Context): SearchRepository {
        return SearchRepositoryImpl(
            context = context,
            networkClient = RetrofitNetworkClient(context)
        )
    }

    fun providePlayerInteractor(track: Track): PlayerInteractor {
        return PlayerInteractorImpl(
            repository = providePlayerRepository(track)
        )
    }

    private fun providePlayerRepository(track: Track): PlayerRepository {
        return PlayerRepositoryImpl(track)
    }
}