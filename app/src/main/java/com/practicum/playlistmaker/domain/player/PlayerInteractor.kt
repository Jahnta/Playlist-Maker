package com.practicum.playlistmaker.domain.player

import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.model.Track

interface PlayerInteractor {
    fun preparePlayer(track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentTrackTime(): String
    fun getPlayerInfo(observer: PlayerInfoObserver)
}
