package de.jbamberger.fhg.repository

/**
 * A generic class that holds a value with its loading status.
 *
 * @param <T>
</T> */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Loading<out T>(val data: T?) : Resource<T>()
    data class Error<out T>(val message: String, val data: T?) : Resource<T>()
}
