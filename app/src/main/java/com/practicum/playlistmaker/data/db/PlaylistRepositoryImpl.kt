package com.practicum.playlistmaker.data.db

import android.util.Log
import com.practicum.playlistmaker.data.converters.AddedTrackDbConverter
import com.practicum.playlistmaker.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.data.db.entity.AddedTrackEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.domain.db.PlaylistRepository
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
        val newPlaylist = appDatabase.playlistDao().getPlaylist(playlist.playlistId)
        emit(playlistDbConverter.map(newPlaylist))
    }

    override fun getTracks(playlist: Playlist): Flow<List<Track>> = flow {
        var tracks: List<AddedTrackEntity> = emptyList()
        for (trackId in playlist.playlistTrackIds.asReversed()) {
            tracks = tracks + appDatabase.addedTrackDao().getAddedTrack(trackId)
        }
        emit(tracks.map { addedTrackDbConverter.map(it) })
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
        for (trackId in playlist.playlistTrackIds) {
            val addedTrackEntity = appDatabase.addedTrackDao().getAddedTrack(trackId)
            if (isNotInOtherPlaylists(addedTrackEntity, playlistEntity)) {
                appDatabase.addedTrackDao().deleteAddedTrack(addedTrackEntity)
                Log.d("DB", "${addedTrackEntity.trackId} is deleted from DB")
            }
        }
        appDatabase.playlistDao().deletePlaylist(playlistEntity)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val addedTrackEntity = addedTrackDbConverter.map(track)
        playlist.playlistTrackIds = playlist.playlistTrackIds + track.trackId
        playlist.playlistTracksCount = playlist.playlistTracksCount.plus(1)
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.addedTrackDao().insertAddedTrack(addedTrackEntity)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        val addedTrackEntity = addedTrackDbConverter.map(track)
        playlist.playlistTracksCount = playlist.playlistTracksCount.minus(1)
        playlist.playlistTrackIds = playlist.playlistTrackIds.filter { it != track.trackId }
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)

        if (isNotInOtherPlaylists(addedTrackEntity, playlistEntity)) {
            appDatabase.addedTrackDao().deleteAddedTrack(addedTrackEntity)
            Log.d("DB", "${addedTrackEntity.trackId} is deleted from DB")
        }
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }

    private suspend fun isNotInOtherPlaylists(track: AddedTrackEntity, fromPlaylist: PlaylistEntity) : Boolean {
        val otherPlaylists = appDatabase.playlistDao().getPlaylists()
            .filter { it.playlistId != fromPlaylist.playlistId }
        val playlistsWithTrack = otherPlaylists.filter { it.playlistTrackIds!!.contains(track.trackId) }
        return playlistsWithTrack.isEmpty()
    }
}