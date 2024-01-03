package com.practicum.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.domain.media.model.NewPlaylistState
import com.practicum.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.launch

class MediaNewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<NewPlaylistState>(NewPlaylistState.Empty)
    val state: LiveData<NewPlaylistState> = _state

    fun addPlayList(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist)
        }
    }

    fun setEmptyState() {
        _state.postValue(NewPlaylistState.Empty)
    }

    fun setNotEmptyState() {
        _state.postValue(NewPlaylistState.NotEmpty)
    }

}