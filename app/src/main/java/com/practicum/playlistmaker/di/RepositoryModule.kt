package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.search.SearchRepository
import com.practicum.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.data.settings.Impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.settings.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<SearchRepository> { SearchRepositoryImpl(androidContext(), get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}