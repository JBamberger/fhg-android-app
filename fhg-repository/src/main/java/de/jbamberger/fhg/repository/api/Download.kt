package de.jbamberger.fhg.repository.api

sealed class Download<out T> {
    data class Success<out T>(val data: T) : Download<T>()
    data class Error<out T>(val message: String) : Download<T>()
}