package com.practicum.playlistmaker.ui.media.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.db.FavouritesInteractor
import com.practicum.playlistmaker.domain.media.model.FavouritesState
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MediaFavouritesViewModel(
    private val favouritesInteractor: FavouritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavouritesState>()
    val state: LiveData<FavouritesState>
        get() = _state

    private var isClickAllowed = true

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    fun getTracks() {
        viewModelScope.launch {
            favouritesInteractor.getFavouriteTracks().collect {
                if (it.isEmpty()) {
                    _state.postValue(FavouritesState.Empty(""))
                } else {
                    _state.postValue(FavouritesState.Content(it))
                }
            }
        }
    }


    private fun renderState(state: FavouritesState) {
        _state.postValue(state)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }

}