package com.practicum.playlistmaker.domain.media.model

import android.net.Uri

data class Playlist(
    val playlistId: Int,
    val playlistTitle: String?,
    val playlistCoverPath: Uri?,
    val playlistDescription: String?,
    var playlistTrackIds: List<String>,
    var playlistTracksCount: Int
)