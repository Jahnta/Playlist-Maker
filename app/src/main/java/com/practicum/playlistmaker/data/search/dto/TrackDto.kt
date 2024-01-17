package com.practicum.playlistmaker.data.search.dto

data class TrackDto(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl60: String,
    val artworkUrl100: String,
    val collectionName: String,
    val country: String,
    val primaryGenreName: String,
    val releaseDate: String,
    val previewUrl: String,
)
