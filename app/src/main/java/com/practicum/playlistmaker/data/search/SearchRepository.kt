package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.creator.Resource
import com.practicum.playlistmaker.domain.search.model.Track

interface SearchRepository {
   fun searchTracks(expression: String) : Resource<List<Track>>
}