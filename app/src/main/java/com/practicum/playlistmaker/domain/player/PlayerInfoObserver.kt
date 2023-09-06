package com.practicum.playlistmaker.domain.player

import com.practicum.playlistmaker.domain.player.model.PlayerInfo

interface PlayerInfoObserver {
    fun onPlayerInfoChanged(playerInfo: PlayerInfo)
}