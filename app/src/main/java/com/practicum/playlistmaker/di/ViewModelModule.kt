package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.player.PlayerRepository
import com.practicum.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.media.view_model.MediaFavouritesViewModel
import com.practicum.playlistmaker.ui.media.view_model.MediaPlaylistsViewModel
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel { (track: Track) ->
        val repository = get<PlayerRepository> { parametersOf(track) }
        PlayerViewModel(PlayerInteractorImpl(repository))
    }

    viewModel {
        MediaFavouritesViewModel()
    }

    viewModel {
        MediaPlaylistsViewModel()
    }


}