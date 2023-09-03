package com.practicum.playlistmaker.data.search.network

import com.practicum.playlistmaker.data.search.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("/search?entity=song")
    fun searchTrack(@Query("term") queryText: String): Call<TrackSearchResponse>
}