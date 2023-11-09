package com.practicum.playlistmaker.domain.search.model

sealed interface SearchFragmentState {
    data class SearchHistory(
        val trackList: List<Track>
    ) : SearchFragmentState
    data class Content(
        val trackList: List<Track>
    ) : SearchFragmentState
    object Loading : SearchFragmentState
    object Empty : SearchFragmentState
    object ConnectionError : SearchFragmentState
}
