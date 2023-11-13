package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.data.search.SearchRepository
import com.practicum.playlistmaker.domain.search.SearchInteractor
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
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