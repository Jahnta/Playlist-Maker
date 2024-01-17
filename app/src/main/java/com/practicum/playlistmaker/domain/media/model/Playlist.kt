package com.practicum.playlistmaker.domain.media.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val playlistId: Int,
    val playlistTitle: String?,
    val playlistCoverPath: Uri?,
    val playlistDescription: String?,
    var playlistTrackIds: List<String>,
    var playlistTracksCount: Int
) : Parcelable