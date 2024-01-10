package com.practicum.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    val updatedPlaylist: MutableLiveData<Playlist> = MutableLiveData()
    fun getPlaylist(playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            playlistInteractor.getPlaylist(playlist).collect {
                updatedPlaylist.postValue(it)
            }
        }
    }

    val tracks: MutableLiveData<List<Track>> = MutableLiveData(emptyList())
    fun getTracks(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.getTracks(playlist).collect { list ->
                tracks.postValue(list)
            }
        }
    }

    suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            playlist.playlistTrackIds = playlist.playlistTrackIds.filter { it != track.trackId }
            playlist.playlistTracksCount = playlist.playlistTracksCount.minus(1)
            playlistInteractor.deleteTrackFromPlaylist(track, playlist)
        }
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            playlistInteractor.removePlaylist(playlist)
        }
    }

}