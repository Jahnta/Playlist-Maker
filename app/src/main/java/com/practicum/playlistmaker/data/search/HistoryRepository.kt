package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.domain.search.model.Track

interface HistoryRepository {
    fun saveTrack(track: Track)
    fun getAllTracks(): List<Track>
    fun clearHistory()
}