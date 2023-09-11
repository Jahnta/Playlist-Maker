package com.practicum.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.player.PlayerInfoObserver
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.model.PlayerInfo
import com.practicum.playlistmaker.domain.player.model.PlayerState

class PlayerViewModel(
    private val interactor: PlayerInteractor,
) : ViewModel() {
    companion object {
        private const val DELAY = 500L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable { updateTimer() }

    private val _playerInfo = MutableLiveData(PlayerInfo(PlayerState.STATE_DEFAULT, "00:00"))
    val playerInfo: LiveData<PlayerInfo> get() = _playerInfo

    init {
        interactor.getPlayerInfo(
            object : PlayerInfoObserver {
                override fun onPlayerInfoChanged(playerInfo: PlayerInfo) {
                    _playerInfo.value = playerInfo
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
                _playerInfo.value = PlayerInfo(PlayerState.STATE_PLAYING, interactor.getCurrentTrackTime())
                handler.postDelayed(timerRunnable, DELAY)
            }

            PlayerState.STATE_PAUSED -> {
                _playerInfo.value = PlayerInfo(PlayerState.STATE_PAUSED, interactor.getCurrentTrackTime())
                handler.removeCallbacks(timerRunnable)
            }

            else -> {
                handler.removeCallbacks(timerRunnable)
                _playerInfo.value = PlayerInfo(PlayerState.STATE_PREPARED, interactor.getCurrentTrackTime())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        destroyPlayer()
    }
}