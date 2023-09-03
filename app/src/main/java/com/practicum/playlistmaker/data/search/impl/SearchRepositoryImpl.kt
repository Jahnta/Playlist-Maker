package com.practicum.playlistmaker.data.search.impl

import com.practicum.playlistmaker.creator.Resource
import com.practicum.playlistmaker.data.search.dto.TrackSearchRequest
import com.practicum.playlistmaker.data.search.dto.TrackSearchResponse
import com.practicum.playlistmaker.data.search.network.NetworkClient
import com.practicum.playlistmaker.data.search.SearchRepository
import com.practicum.playlistmaker.domain.search.model.Track

class SearchRepositoryImpl(private val networkClient: NetworkClient) : SearchRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error(message = "Проверьте подключение к интернету")
            }

            200 -> {
                Resource.Success(
                    data = (response as TrackSearchResponse).results.map {
                        Track(
                            it.trackId,
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.collectionName,
                            it.country,
                            it.primaryGenreName,
                            it.releaseDate,
                            it.previewUrl
                        )
                    })
            }

            else -> {
                Resource.Error(message = "Ошибка сервера")
            }
        }
    }

}