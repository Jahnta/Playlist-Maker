package com.practicum.playlistmaker.domain.db

import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesInteractor {

    fun getFavouriteTracks(): Flow<List<Track>>

    suspend fun addToFavoriteTrackList(track: Track)

    suspend fun removeFromFavoriteTrackList(track: Track)

    suspend fun isFavorite(track: Track) : Boolean
}