package com.practicum.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey
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
)