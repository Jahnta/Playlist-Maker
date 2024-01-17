package com.practicum.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val interactor: PlaylistInteractor) : ViewModel() {

    val playlists: MutableLiveData<List<Playlist>> = MutableLiveData<List<Playlist>>()
    fun getPlaylists() : LiveData<List<Playlist>> {
        viewModelScope.launch {
            interactor.getPlaylists()
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