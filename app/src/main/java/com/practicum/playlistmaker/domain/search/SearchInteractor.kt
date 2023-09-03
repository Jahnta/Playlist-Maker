package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.search.model.Track

interface SearchInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}