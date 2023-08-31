package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.PlayerStates
import com.practicum.playlistmaker.domain.models.Track

interface MediaPlayerInteractor {

    fun preparePlayer(track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun destroyPlayer()
    fun getPlayerState() : PlayerStates

}