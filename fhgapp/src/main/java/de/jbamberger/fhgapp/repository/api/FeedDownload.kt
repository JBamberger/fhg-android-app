package de.jbamberger.fhgapp.repository.api

sealed class FeedDownload<out T> {
    data class Success<out T>(val data: T) : FeedDownload<T>()
    data class Error<out T>(val message: String) : FeedDownload<T>()
}