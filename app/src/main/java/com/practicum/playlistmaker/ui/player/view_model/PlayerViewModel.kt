package com.practicum.playlistmaker.ui.player.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.db.FavouritesInteractor
import com.practicum.playlistmaker.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.domain.player.PlayerInfoObserver
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.model.PlayerInfo
import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favouritesInteractor: FavouritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private var timerJob: Job? = null

    private val _playerInfo = MutableLiveData(PlayerInfo(PlayerState.STATE_DEFAULT, "00:00"))
    val playerInfo: LiveData<PlayerInfo> get() = _playerInfo

    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> get() = _isFavourite


    init {
        Log.d("D", "viewmodel created")
        playerInteractor.getPlayerInfo(
            object : PlayerInfoObserver {
                override fun onPlayerInfoChanged(playerInfo: PlayerInfo) {
                    _playerInfo.value = playerInfo
                }
            }
        )
        viewModelScope.launch {
            _isFavourite.value = favouritesInteractor.isFavorite(track)
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
    }

    fun destroyPlayer() {
        playerInteractor.releasePlayer()
        timerJob = null
    }

    fun playbackControl() {
        when (playerInfo.value?.playerState) {
            PlayerState.STATE_PLAYING -> pausePlayer()
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> startPlayer()
            else -> {}
        }
        updateTimer()
    }

    private fun updateTimer() {
        when (playerInfo.value?.playerState) {
            PlayerState.STATE_PLAYING -> {
                timerJob = viewModelScope.launch {
                    while (playerInfo.value!!.playerState == PlayerState.STATE_PLAYING) {
                        _playerInfo.value =
                            PlayerInfo(PlayerState.STATE_PLAYING, playerInteractor.getCurrentTrackTime())
                        delay(DELAY)
                    }
                }
            }

            PlayerState.STATE_PAUSED -> {
                timerJob?.cancel()
            }

            else -> {
                _playerInfo.value =
                    PlayerInfo(PlayerState.STATE_PLAYING, playerInteractor.getCurrentTrackTime())
            }
        }
    }

    fun processFavouriteButtonClicked() {
        viewModelScope.launch {
            if (_isFavourite.value == true) {
                favouritesInteractor.removeFromFavoriteTrackList(track)
                _isFavourite.postValue(false)
            } else {
                favouritesInteractor.addToFavoriteTrackList(track)
                _isFavourite.postValue(true)
            }
        }
    }

    val playlists: MutableLiveData<List<Playlist>> = MutableLiveData<List<Playlist>>(emptyList())

    fun getPlaylists(): LiveData<List<Playlist>> {
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

    val isInPlaylist = MutableLiveData(false)
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            if (playlist.playlistTrackIds.contains(track.trackId)) {
                isInPlaylist.postValue(true)
            } else {
                isInPlaylist.postValue(false)
                playlistInteractor.addTrackToPlaylist(track, playlist)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        destroyPlayer()
    }

    companion object {
        private const val DELAY = 300L
    }
}