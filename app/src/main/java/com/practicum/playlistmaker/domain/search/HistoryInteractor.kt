package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.search.model.Track

interface HistoryInteractor {
    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()
}