package com.practicum.playlistmaker.domain.media.model

import com.practicum.playlistmaker.domain.search.model.Track

sealed interface FavouritesState {

    object Loading: FavouritesState

    data class Content(
        val tracks: List<Track>
    ) : FavouritesState

    data class Empty(
        val message: String
    ) : FavouritesState

}