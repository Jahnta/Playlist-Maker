package com.practicum.playlistmaker.data.search.dto

class TrackSearchResponse(
    val resultCount: String,
    val results: List<TrackDto>
) : Response()