package com.practicum.playlistmaker.data.converters

import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.domain.media.model.Playlist

class PlaylistDbConverter {

    private val gson = Gson()
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistTitle = playlist.playlistTitle,
            playlistCoverPath = playlist.playlistCoverPath.toString(),
            playlistDescription = playlist.playlistDescription,
            playlistTrackIds = gson.toJson(playlist.playlistTrackIds) ,
            playlistTracksCount = playlist.playlistTracksCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlistEntity.playlistId,
            playlistTitle = playlistEntity.playlistTitle,
            playlistCoverPath = playlistEntity.playlistCoverPath?.toUri(),
            playlistDescription = playlistEntity.playlistDescription,
            playlistTrackIds = gson.fromJson(playlistEntity.playlistTrackIds, object : TypeToken<List<String>>() {}.type),
            playlistTracksCount = playlistEntity.playlistTracksCount
        )
    }
}