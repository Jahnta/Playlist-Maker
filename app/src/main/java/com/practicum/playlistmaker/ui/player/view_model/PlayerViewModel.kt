package com.practicum.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.player.PlayerStateObserver
import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

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

    init {
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
    }

    fun pausePlayer() {
        interactor.pausePlayer()
    }

    fun destroyPlayer() {
        interactor.releasePlayer()
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
                _timer.value = SimpleDateFormat("mm:ss", Locale.getDefault()).format(interactor.getCurrentTrackTime())
                handler.postDelayed(timerRunnable, DELAY)
            }

            PlayerState.STATE_PAUSED -> handler.removeCallbacks(timerRunnable)
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