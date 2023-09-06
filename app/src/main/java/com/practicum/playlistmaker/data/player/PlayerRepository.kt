package com.practicum.playlistmaker.data.player

import com.practicum.playlistmaker.domain.player.PlayerStateObserver
import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.model.Track

interface PlayerRepository {
    fun preparePlayer(track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentTrackTime(): String
    fun getPlayerState(observer: PlayerStateObserver)
    fun getPlayerStateNew() : PlayerState

}