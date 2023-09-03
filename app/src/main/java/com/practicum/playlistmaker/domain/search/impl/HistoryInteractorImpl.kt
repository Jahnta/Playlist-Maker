package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.data.search.HistoryRepository
import com.practicum.playlistmaker.domain.search.HistoryInteractor
import com.practicum.playlistmaker.domain.search.model.Track

class HistoryInteractorImpl(
    private val repository: HistoryRepository
) : HistoryInteractor {
    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun getAllTracks(): List<Track> {
        return repository.getAllTracks()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}