package com.practicum.playlistmaker.data.converters

import com.practicum.playlistmaker.data.db.entity.AddedTrackEntity
import com.practicum.playlistmaker.domain.search.model.Track

class AddedTrackDbConverter {
    fun map(addedTrack: AddedTrackEntity) : Track {
        return Track(
            trackId = addedTrack.trackId,
            trackName = addedTrack.trackName,
            artistName = addedTrack.artistName,
            trackTimeMillis =addedTrack.trackTimeMillis,
            artworkUrl60 =addedTrack.artworkUrl60,
            artworkUrl100 =addedTrack.artworkUrl100,
            collectionName = addedTrack.collectionName,
            country = addedTrack.country,
            primaryGenreName = addedTrack.primaryGenreName,
            releaseDate = addedTrack.releaseDate,
            previewUrl = addedTrack.previewUrl
        )
    }
    fun map(track: Track) : AddedTrackEntity {
        return AddedTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis =track.trackTimeMillis,
            artworkUrl60 = track.artworkUrl60,
            artworkUrl100 =track.artworkUrl100,
            collectionName = track.collectionName,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            previewUrl = track.previewUrl
        )
    }
}