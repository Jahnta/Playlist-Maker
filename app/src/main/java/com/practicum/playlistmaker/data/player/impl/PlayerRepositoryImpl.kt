package com.practicum.playlistmaker.data.player.impl

import android.media.MediaPlayer
import android.util.Log
import com.practicum.playlistmaker.data.player.PlayerRepository
import com.practicum.playlistmaker.domain.player.PlayerInfoObserver
import com.practicum.playlistmaker.domain.player.model.PlayerInfo
import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(
    track: Track,
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

    private var playerInfo: PlayerInfo = PlayerInfo(PlayerState.STATE_DEFAULT,"00:00")
    private val observers = mutableListOf<PlayerInfoObserver>()

    init {
        preparePlayer(track)
    }

    override fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerInfo = PlayerInfo(PlayerState.STATE_PREPARED, "00:00")
            Log.d("PLAYER", "${playerInfo.playerState} ${playerInfo.elapsedTime}")
            notifyPlayerInfoChanged(playerInfo)
        }
        mediaPlayer.setOnCompletionListener {
            playerInfo = PlayerInfo(PlayerState.STATE_PREPARED, "00:00")
            Log.d("PLAYER", "${playerInfo.playerState} ${playerInfo.elapsedTime}")
            notifyPlayerInfoChanged(playerInfo)
        }
    }

    override fun startPlayer() {
        Log.d("PLAYER", "${playerInfo.playerState} ${playerInfo.elapsedTime}")
        mediaPlayer.start()
        playerInfo = PlayerInfo(PlayerState.STATE_PLAYING, getCurrentTrackTime())
        Log.d("PLAYER", "${playerInfo.playerState} ${playerInfo.elapsedTime}")
        notifyPlayerInfoChanged(playerInfo)
    }

    override fun pausePlayer() {
        if (playerInfo.playerState == PlayerState.STATE_PLAYING) {
            mediaPlayer.pause()
            playerInfo = PlayerInfo(PlayerState.STATE_PAUSED, getCurrentTrackTime())
            Log.d("PLAYER", "${playerInfo.playerState} ${playerInfo.elapsedTime}")
            notifyPlayerInfoChanged(playerInfo)
        }
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentTrackTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun getPlayerInfo(observer: PlayerInfoObserver) {
        observers.add(observer)
        observer.onPlayerInfoChanged(playerInfo)
    }

    private fun notifyPlayerInfoChanged(playerInfo: PlayerInfo) {
        observers.forEach { it.onPlayerInfoChanged(playerInfo) }
    }

}