package de.jbamberger.fhg.repository

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

sealed class NetworkState {
    object LOADED : NetworkState()
    object LOADING : NetworkState()
    data class ERROR(val message: String) : NetworkState()
}