package com.practicum.playlistmaker.domain.search.model

sealed interface SearchActivityState {
    data class SearchHistory(
        val trackList: List<Track>
    ) : SearchActivityState
    data class Content(
        val trackList: List<Track>
    ) : SearchActivityState
    object Loading : SearchActivityState
    object Empty : SearchActivityState
    object ConnectionError : SearchActivityState
}
