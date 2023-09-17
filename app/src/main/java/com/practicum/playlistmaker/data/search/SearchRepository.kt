package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.domain.search.model.Track

interface SearchRepository {
   fun searchTracks(expression: String) : Resource<List<Track>>
   fun saveTrack(track: Track)
   fun getAllTracks(): List<Track>
   fun clearHistory()
}