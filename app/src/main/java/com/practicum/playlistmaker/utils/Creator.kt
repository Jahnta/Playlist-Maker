package com.practicum.playlistmaker.utils

import com.practicum.playlistmaker.data.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.domain.impl.MediaPlayerInteractorImpl

object Creator {

    private fun provideMediaPlayerRepository() = MediaPlayerRepositoryImpl()

    fun provideMediaPlayerInteractor() = MediaPlayerInteractorImpl(provideMediaPlayerRepository())
}