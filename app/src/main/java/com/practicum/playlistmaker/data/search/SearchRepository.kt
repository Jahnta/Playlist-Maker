package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
   fun searchTracks(expression: String) : Flow<Resource<List<Track>>>
   fun saveTrack(track: Track)
   fun getAllTracks(): List<Track>
   fun clearHistory()
}