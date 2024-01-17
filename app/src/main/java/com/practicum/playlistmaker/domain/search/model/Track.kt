package com.practicum.playlistmaker.domain.search.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl60: String,
    val artworkUrl100: String,
    val collectionName: String,
    val country: String,
    val primaryGenreName: String,
    val releaseDate: String?,
    val previewUrl: String?,
    val isFavourite: Boolean = false,
) : Parcelable

