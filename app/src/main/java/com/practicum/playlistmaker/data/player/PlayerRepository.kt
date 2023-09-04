package com.practicum.playlistmaker.data.player

import com.practicum.playlistmaker.domain.player.PlayerStateObserver
import com.practicum.playlistmaker.domain.search.model.Track

interface PlayerRepository {
    fun preparePlayer(track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun getPlayerState(observer: PlayerStateObserver)
    fun getCurrentTrackTime(): Long
    fun releasePlayer()

}