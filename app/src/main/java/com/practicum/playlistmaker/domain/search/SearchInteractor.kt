package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>
    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }


    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()
}