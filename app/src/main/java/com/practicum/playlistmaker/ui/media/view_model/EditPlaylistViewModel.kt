package com.practicum.playlistmaker.ui.media.view_model

import com.practicum.playlistmaker.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val interactor: PlaylistInteractor
) : NewPlaylistViewModel(interactor) {
    fun updatePlaylist(playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            interactor.updatePlaylist(playlist)
        }
    }
}