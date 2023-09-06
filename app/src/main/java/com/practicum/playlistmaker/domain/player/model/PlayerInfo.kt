package com.practicum.playlistmaker.domain.player.model

data class PlayerInfo(
    var playerState: PlayerState,
    val elapsedTime: String
)
