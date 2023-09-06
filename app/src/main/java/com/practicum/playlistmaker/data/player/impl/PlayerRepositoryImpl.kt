package com.practicum.playlistmaker.data.player.impl

import android.media.MediaPlayer
import android.util.Log
import com.practicum.playlistmaker.data.player.PlayerRepository
import com.practicum.playlistmaker.domain.player.PlayerStateObserver
import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.model.Track
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(track: Track) : PlayerRepository {

    private var mediaPlayer = MediaPlayer()
    private var playerState: PlayerState = PlayerState.STATE_DEFAULT
    private val observers = mutableListOf<PlayerStateObserver>()

    override fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.STATE_PREPARED
            notifyPlayerStateChanged(playerState)
            Log.d("AAA", "${playerState}")
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.STATE_PREPARED
            notifyPlayerStateChanged(playerState)
            Log.d("AAA", "${playerState}")
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
        notifyPlayerStateChanged(playerState)
    }

    override fun pausePlayer() {
        if (playerState == PlayerState.STATE_PLAYING) {
            mediaPlayer.pause()
            playerState = PlayerState.STATE_PAUSED
            notifyPlayerStateChanged(playerState)
        }
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentTrackTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun getPlayerState(observer: PlayerStateObserver) {
        observers.add(observer)
        observer.onPlayerStateChanged(playerState)
    }

    private fun notifyPlayerStateChanged(state: PlayerState) {
        observers.forEach { it.onPlayerStateChanged(state) }
    }

    override fun getPlayerStateNew() : PlayerState {
        return playerState
    }
}