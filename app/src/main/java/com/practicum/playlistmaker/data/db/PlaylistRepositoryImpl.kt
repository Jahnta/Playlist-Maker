package com.practicum.playlistmaker.data.db

import android.util.Log
import androidx.room.PrimaryKey
import com.practicum.playlistmaker.data.converters.AddedTrackDbConverter
import com.practicum.playlistmaker.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.domain.db.PlaylistRepository
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.forEach

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val addedTrackDbConverter: AddedTrackDbConverter,
) : PlaylistRepository {
    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override fun getPlaylist(playlist: Playlist): Flow<Playlist> = flow {
        val new_playlist = appDatabase.playlistDao().getPlaylist(playlist.playlistId)
        emit(playlistDbConverter.map(new_playlist))
    }

    override fun getTracks(playlist: Playlist): Flow<List<Track>> = flow {
        var addedTracks = appDatabase.addedTrackDao().getAddedTracks()
        var tracks = addedTracks
            .filter { playlist.playlistTrackIds.contains(it.trackId) }
            .map {
                addedTrackDbConverter.map(it)
            }
        emit(tracks)
    }

    override fun getPlaylistDuration(playlist: Playlist): Flow<String> = flow {
        var addedTracks = appDatabase.addedTrackDao().getAddedTracks()
        var tracks = addedTracks
            .filter { playlist.playlistTrackIds.contains(it.trackId) }
            .map {
                addedTrackDbConverter.map(it)
            }
        tracks.forEach {
        }
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun removePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().deletePlaylist(playlistEntity)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val addedTrackEntity = addedTrackDbConverter.map(track)
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.addedTrackDao().insertAddedTrack(addedTrackEntity)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        val addedTrackEntity = addedTrackDbConverter.map(track)
        val playlistEntity = playlistDbConverter.map(playlist)
        val otherPlaylists = appDatabase.playlistDao().getPlaylists()
            .filter { it.playlistId != playlistEntity.playlistId }
            .filter { it.playlistTrackIds!!.contains(track.trackId) }
        if (otherPlaylists.isNullOrEmpty()) {
            appDatabase.addedTrackDao().deleteAddedTrack(addedTrackEntity)
        }
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }
}