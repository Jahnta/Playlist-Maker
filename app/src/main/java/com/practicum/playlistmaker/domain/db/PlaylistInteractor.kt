package com.practicum.playlistmaker.domain.db

import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun removePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(track: Track, playlist: Playlist)

}