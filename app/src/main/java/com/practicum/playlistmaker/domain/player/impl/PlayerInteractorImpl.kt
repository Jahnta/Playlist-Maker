package com.practicum.playlistmaker.domain.player.impl

import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.data.player.PlayerRepository
import com.practicum.playlistmaker.domain.player.PlayerStateObserver
import com.practicum.playlistmaker.domain.search.model.Track

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(track: Track) {
        repository.preparePlayer(track)
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun getPlayerState(observer: PlayerStateObserver){
        return repository.getPlayerState(observer)
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun getCurrentTrackTime(): Long {
        return repository.getCurrentTrackTime()
    }



    override fun releasePlayer() {
        repository.releasePlayer()
    }
}