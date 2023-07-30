package com.practicum.playlistmaker.creator

import com.practicum.playlistmaker.data.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.domain.impl.MediaPlayerInteractorImpl

object Creator {

    fun provideMediaPlayerRepository() = MediaPlayerRepositoryImpl()

    fun provideMediaPlayerInteractor() = MediaPlayerInteractorImpl(provideMediaPlayerRepository())
}