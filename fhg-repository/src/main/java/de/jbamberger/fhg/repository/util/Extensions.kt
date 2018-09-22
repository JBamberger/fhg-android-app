package de.jbamberger.fhg.repository.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

fun <X, Y> LiveData<X>.map(function: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, function)
}

fun <X, Y> LiveData<X>.switchMap(function: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this, function)
}