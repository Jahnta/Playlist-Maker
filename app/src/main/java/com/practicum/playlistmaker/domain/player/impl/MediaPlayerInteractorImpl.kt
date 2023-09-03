package com.practicum.playlistmaker.domain.player.impl

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.domain.player.MediaPlayerInteractor
import com.practicum.playlistmaker.data.player.MediaPlayerRepository
import com.practicum.playlistmaker.domain.player.model.PlayerStates
import com.practicum.playlistmaker.domain.search.model.Track

class MediaPlayerInteractorImpl(repository: MediaPlayerRepository) : MediaPlayerInteractor {

    private val mediaPlayer = MediaPlayer()
    private var playerState = PlayerStates.STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    val DELAY = 500L


    override fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerStates.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(updatePlayTimeTask)
            playerState = PlayerStates.STATE_PREPARED
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerStates.STATE_PLAYING
        handler.post(updatePlayTimeTask)
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerStates.STATE_PAUSED
        handler.removeCallbacks(updatePlayTimeTask)
    }

    override fun destroyPlayer() {
        handler.removeCallbacks(updatePlayTimeTask)
        mediaPlayer.release()
    }

    private val updatePlayTimeTask: Runnable = object : Runnable {
        override fun run() {
            if (playerState == PlayerStates.STATE_PLAYING) {
                handler.postDelayed(this, DELAY)
            }
        }
    }
    override fun getPlayerState(): PlayerStates {
        return playerState
    }
}