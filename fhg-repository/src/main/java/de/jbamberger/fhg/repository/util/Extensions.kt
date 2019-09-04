package de.jbamberger.fhg.repository.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import de.jbamberger.fhg.repository.data.FeedMedia

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

fun <X, Y> LiveData<X>.map(function: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, function)
}

fun <X, Y> LiveData<X>.switchMap(function: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this, function)
}

/**
 * returns width / height ratio or null, if not available
 *
 */
fun FeedMedia.getSaveImgSize(): ImgSize? {
    val width = this.media_details.width
    val height = this.media_details.height
    return Pair(width, height)
}

typealias ImgSize = Pair<Int, Int>

fun ImgSize.formatAsAspectRatio(fixedDirection: String = "H"): String {
    return "$fixedDirection,${this.first}:${this.second}"
}