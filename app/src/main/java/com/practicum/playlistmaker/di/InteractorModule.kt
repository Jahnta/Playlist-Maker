package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.db.FavouritesInteractor
import com.practicum.playlistmaker.domain.media.impl.FavouritesInteractorImpl
import com.practicum.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.search.SearchInteractor
import com.practicum.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.domain.settings.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val interactorModule = module {

    single<SearchInteractor> { SearchInteractorImpl(get()) }

    single<SettingsInteractor> { SettingsInteractorImpl(get()) }
    single<SharingInteractor> { SharingInteractorImpl(get(), get()) }

    factory { (track: Track) ->
        PlayerInteractorImpl(get { parametersOf(track) })
    }

    single<FavouritesInteractor> {
        FavouritesInteractorImpl(get())
    }
}