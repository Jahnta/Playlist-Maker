package com.practicum.playlistmaker.ui.player.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.player.PlayerInfoObserver
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.model.PlayerInfo
import com.practicum.playlistmaker.domain.player.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val interactor: PlayerInteractor,
) : ViewModel() {
    companion object {
        private const val DELAY = 300L
    }

    private var timerJob: Job? = null

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
        timerJob?.cancel()
        when (playerInfo.value?.playerState) {
            PlayerState.STATE_PLAYING -> {
                timerJob = viewModelScope.launch {
                    while (playerInfo.value!!.playerState == PlayerState.STATE_PLAYING) {
                        _playerInfo.value =
                            PlayerInfo(PlayerState.STATE_PLAYING, interactor.getCurrentTrackTime())
                        delay(DELAY)
                    }
                }
            }

            PlayerState.STATE_PAUSED -> {
                timerJob?.cancel()
            }

            else -> {
                _playerInfo.value =
                    PlayerInfo(PlayerState.STATE_PLAYING, interactor.getCurrentTrackTime())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        destroyPlayer()
    }
}