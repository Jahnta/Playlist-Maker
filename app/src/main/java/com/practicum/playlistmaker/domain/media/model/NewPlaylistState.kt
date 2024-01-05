package com.practicum.playlistmaker.domain.media.model

sealed interface NewPlaylistState {
    object Empty : NewPlaylistState

    object NotEmpty : NewPlaylistState
}