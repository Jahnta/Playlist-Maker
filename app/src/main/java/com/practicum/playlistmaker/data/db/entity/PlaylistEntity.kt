package com.practicum.playlistmaker.data.db.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,
    @ColumnInfo(defaultValue = "")
    val playlistTitle: String?,
    val playlistCoverPath: String?,
    val playlistDescription: String?,
    @ColumnInfo(defaultValue = "0")
    val playlistTrackIds: String? = "",
    @ColumnInfo(defaultValue = "0")
    var playlistTracksCount : Int = 0
)