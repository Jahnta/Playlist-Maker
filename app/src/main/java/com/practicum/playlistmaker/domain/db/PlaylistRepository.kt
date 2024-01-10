package com.practicum.playlistmaker.domain.db

import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylist(playlist: Playlist): Flow<Playlist>
    fun getTracks(playlist: Playlist): Flow<List<Track>>
    fun getPlaylistDuration(playlist: Playlist): Flow<String>

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun removePlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist)


}