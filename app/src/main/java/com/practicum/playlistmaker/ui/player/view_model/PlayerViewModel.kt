package com.practicum.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.player.PlayerStateObserver
import com.practicum.playlistmaker.domain.player.model.PlayerInfo
import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.model.Track

class PlayerViewModel(
    track: Track
) : ViewModel() {
    companion object {
        private const val DELAY = 500L

        fun getViewModelFactory(track: Track): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlayerViewModel(
                        track
                    ) as T
                }
            }
    }

    private val interactor = Creator.providePlayerInteractor(track)
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable { updateTimer() }

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _timer = MutableLiveData("00:00")
    val timeProgress: LiveData<String>
        get() = _timer

    private val _playerInfo = MutableLiveData<PlayerInfo>()
    val playerInfo: LiveData<PlayerInfo> get() = _playerInfo

    init {
        Log.d("AAA", "${interactor.getPlayerStateNew()}")
        interactor.preparePlayer(track)
        Log.d("AAA", "${interactor.getPlayerStateNew()}")
        interactor.getPlayerState(
            object : PlayerStateObserver {
                override fun onPlayerStateChanged(state: PlayerState) {
                    _playerState.value = state
                }
            }
        )
    }

    private fun startPlayer() {
        interactor.startPlayer()
        Log.d("AAA", "${interactor.getPlayerStateNew()}")
    }

    fun pausePlayer() {
        interactor.pausePlayer()
        Log.d("AAA", "${interactor.getPlayerStateNew()}")
    }

    fun destroyPlayer() {
        interactor.releasePlayer()
        Log.d("AAA", "${interactor.getPlayerStateNew()}")
        updateTimer()
    }

    fun playbackControl() {
        when (playerState.value) {
            PlayerState.STATE_PLAYING -> pausePlayer()
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> startPlayer()
            else -> {}
        }
        updateTimer()
    }

    private fun updateTimer() {
        when (playerState.value) {
            PlayerState.STATE_PLAYING -> {
                _timer.value = interactor.getCurrentTrackTime()
                handler.postDelayed(timerRunnable, DELAY)
            }

            PlayerState.STATE_PAUSED -> {
                handler.removeCallbacks(timerRunnable)
            }

            else -> {
                handler.removeCallbacks(timerRunnable)
                _timer.value = "00:00"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        destroyPlayer()
    }
}