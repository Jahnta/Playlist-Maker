package com.practicum.playlistmaker.domain.media.impl

import com.practicum.playlistmaker.domain.db.FavouritesInteractor
import com.practicum.playlistmaker.domain.db.FavouritesRepository
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class FavouritesInteractorImpl(
    private val favouritesRepository: FavouritesRepository
): FavouritesInteractor {
    override fun getFavouriteTracks(): Flow<List<Track>> {
        return favouritesRepository.getFavouriteTracks()
    }

    override suspend fun addToFavoriteTrackList(track: Track) {
        favouritesRepository.addToFavoriteTrackList(track)
    }

    override suspend fun removeFromFavoriteTrackList(track: Track) {
        favouritesRepository.removeFromFavoriteTrackList(track)
    }

    override suspend fun isFavorite(track: Track): Boolean {
        return favouritesRepository.isFavorite(track)
    }

}