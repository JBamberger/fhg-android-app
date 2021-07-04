/*
 *    Copyright 2021 Jannik Bamberger
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.jbamberger.fhgapp.repository.util

import android.os.Build
import android.text.Html
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import de.jbamberger.fhgapp.repository.data.FeedMedia

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
fun FeedMedia.getSaveImgSize(): ImgSize {
    val width = this.media_details.width
    val height = this.media_details.height
    return Pair(width, height)
}

typealias ImgSize = Pair<Int, Int>

fun ImgSize.formatAsAspectRatio(fixedDirection: String = "H"): String {
    return "$fixedDirection,${this.first}:${this.second}"
}

fun String.unescapeHtml(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION") Html.fromHtml(this)
    }.toString()
}
