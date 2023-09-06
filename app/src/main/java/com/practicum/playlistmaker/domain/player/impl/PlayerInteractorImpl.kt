package com.practicum.playlistmaker.domain.player.impl

import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.data.player.PlayerRepository
import com.practicum.playlistmaker.domain.player.PlayerInfoObserver
import com.practicum.playlistmaker.domain.player.model.PlayerState
import com.practicum.playlistmaker.domain.search.model.Track

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(track: Track) {
        repository.preparePlayer(track)
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }

    override fun getCurrentTrackTime(): String {
        return repository.getCurrentTrackTime()
    }

    override fun getPlayerInfo(observer: PlayerInfoObserver){
        return repository.getPlayerInfo(observer)
    }

}