package com.practicum.playlistmaker.domain.media.model

interface PlaylistsState {
    object Loading: PlaylistsState

    data class Content(
        val tracks: List<Playlist>
    ) : PlaylistsState

    data class Empty(
        val message: String
    ) : PlaylistsState
}