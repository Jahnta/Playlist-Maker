package com.practicum.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.launch

class MediaPlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val playlists = MutableLiveData<List<Playlist>>()
    fun getPlaylists() : LiveData<List<Playlist>> {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collect {
                    if (it.isNotEmpty()) {
                        playlists.postValue(it)
                    } else {
                        playlists.postValue(emptyList())
                    }
                }
        }

        return playlists
    }

}