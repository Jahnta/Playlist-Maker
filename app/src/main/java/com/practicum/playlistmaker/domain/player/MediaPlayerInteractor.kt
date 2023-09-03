package com.practicum.playlistmaker.domain.player

import com.practicum.playlistmaker.domain.player.model.PlayerStates
import com.practicum.playlistmaker.domain.search.model.Track

interface MediaPlayerInteractor {

    fun preparePlayer(track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun destroyPlayer()
    fun getPlayerState() : PlayerStates

}