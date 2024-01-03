package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.converters.AddedTrackDbConverter
import com.practicum.playlistmaker.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.data.converters.TrackDbConvertor
import com.practicum.playlistmaker.data.db.FavouritesRepositoryImpl
import com.practicum.playlistmaker.data.db.PlaylistRepositoryImpl
import com.practicum.playlistmaker.data.search.SearchRepository
import com.practicum.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.data.settings.Impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.settings.SettingsRepository
import com.practicum.playlistmaker.domain.db.FavouritesRepository
import com.practicum.playlistmaker.domain.db.PlaylistRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<SearchRepository> { SearchRepositoryImpl(androidContext(), get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    factory { TrackDbConvertor() }
    factory { PlaylistDbConverter() }
    factory { AddedTrackDbConverter() }

    single<FavouritesRepository> {
        FavouritesRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }
}