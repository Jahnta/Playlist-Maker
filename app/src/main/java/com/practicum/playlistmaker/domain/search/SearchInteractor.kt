package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.utils.ErrorType
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>

    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()
}