package com.practicum.playlistmaker.data.player.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.data.player.PlayerRepository
import com.practicum.playlistmaker.domain.player.PlayerStateObserver
import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.model.Track
import java.io.IOException

class PlayerRepositoryImpl(track: Track) : PlayerRepository {

    private var mediaPlayer = MediaPlayer()
    private var currentTrackTime: Long = 0L
    private var startTime: Long = 0L
    private var playerState: PlayerState = PlayerState.STATE_DEFAULT
    private val observers = mutableListOf<PlayerStateObserver>()

    init {
        preparePlayer(track)
    }

    override fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.STATE_PREPARED
            notifyPlayerStateChanged(playerState)
        }
        mediaPlayer.setOnCompletionListener {
            currentTrackTime = 0L
            startTime = 0L
            playerState = PlayerState.STATE_PREPARED
            notifyPlayerStateChanged(playerState)
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
        startTime = System.currentTimeMillis() - currentTrackTime
        notifyPlayerStateChanged(playerState)
    }

    override fun pausePlayer() {
        if (playerState == PlayerState.STATE_PLAYING) {
            currentTrackTime = System.currentTimeMillis() - startTime
            mediaPlayer.pause()
            playerState = PlayerState.STATE_PAUSED
            notifyPlayerStateChanged(playerState)
        }
    }

    override fun getPlayerState(observer: PlayerStateObserver) {
        observers.add(observer)
        observer.onPlayerStateChanged(playerState)
    }

    override fun getCurrentTrackTime(): Long {
        if (playerState == PlayerState.STATE_PLAYING) {
            currentTrackTime = System.currentTimeMillis() - startTime
        }
        return currentTrackTime
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    private fun notifyPlayerStateChanged(state: PlayerState) {
        playerState = state
        observers.forEach { it.onPlayerStateChanged(state) }
    }
}