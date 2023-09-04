package com.practicum.playlistmaker.domain.player

import com.practicum.playlistmaker.domain.search.model.Track

interface PlayerInteractor {
    fun preparePlayer(track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun getPlayerState(observer: PlayerStateObserver)
    fun getCurrentTrackTime(): Long
    fun releasePlayer()
}
