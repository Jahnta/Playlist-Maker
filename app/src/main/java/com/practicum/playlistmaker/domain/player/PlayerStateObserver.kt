package com.practicum.playlistmaker.domain.player

import com.practicum.playlistmaker.domain.player.model.PlayerState

interface PlayerStateObserver {
    fun onPlayerStateChanged(state: PlayerState)
}