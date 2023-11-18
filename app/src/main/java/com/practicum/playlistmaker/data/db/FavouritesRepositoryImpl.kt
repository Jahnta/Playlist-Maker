package com.practicum.playlistmaker.data.db

import com.practicum.playlistmaker.data.converters.TrackDbConvertor
import com.practicum.playlistmaker.domain.db.FavouritesRepository
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class FavouritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
): FavouritesRepository {
    override fun getFavouriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks().reversed()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun addToFavoriteTrackList(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun removeFromFavoriteTrackList(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    override suspend fun isFavorite(track: Track): Boolean = withContext(Dispatchers.IO) {
        val favouriteTracks = appDatabase.trackDao().getTracksId()
        favouriteTracks.contains(track.trackId)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map {
            track -> trackDbConvertor.map(track)
        }
    }

}