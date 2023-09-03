package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.creator.Resource
import com.practicum.playlistmaker.data.search.SearchRepository
import com.practicum.playlistmaker.domain.search.SearchInteractor
import java.util.concurrent.Executors

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun searchTracks(expression: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }
                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }
}