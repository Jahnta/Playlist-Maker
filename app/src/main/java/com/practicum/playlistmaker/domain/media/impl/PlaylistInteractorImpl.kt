package com.practicum.playlistmaker.domain.media.impl

import com.practicum.playlistmaker.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.domain.db.PlaylistRepository
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
): PlaylistInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override fun getPlaylist(playlist: Playlist): Flow<Playlist> {
        return repository.getPlaylist(playlist)
    }

    override fun getTracks(playlist: Playlist): Flow<List<Track>> {
        return repository.getTracks(playlist)
    }

    override fun getPlaylistDuration(playlist: Playlist): Flow<String> {
        return repository.getPlaylistDuration(playlist)
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun removePlaylist(playlist: Playlist) {
        repository.removePlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        repository.deleteTrackFromPlaylist(track, playlist)
    }
}