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

package de.jbamberger.fhgapp.util

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import de.jbamberger.fhgapp.repository.data.FeedMedia
import java.io.InputStream
import kotlin.math.abs


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class FeedMediaLoader internal constructor(
    urlLoader: ModelLoader<GlideUrl, InputStream>
) : BaseGlideUrlLoader<FeedMedia>(urlLoader) {

    override fun handles(model: FeedMedia): Boolean {
        return true
    }

    override fun getUrl(model: FeedMedia?, width: Int, height: Int, options: Options?): String? {
        if (model == null || model.media_type != "image" || width <= 0 || height <= 0) {
            return null
        }

        val aspectRatio = width / height
        val size = width * height

        val x = model.media_details.sizes.values
        val chosenSize = x.maxWithOrNull { o1, o2 ->
            val asp1 = o1.width / o1.height
            val size1 = o1.width * o1.height
            val asp2 = o2.width / o2.height
            val size2 = o2.width * o2.height

            when (asp1) {
                asp2 -> compareValues(abs(size1 - size), abs(size2 - size))
                else -> compareValues(abs(asp1 - aspectRatio), abs(asp2 - aspectRatio))
            }
        }
        return chosenSize?.source_url ?: model.source_url
    }

    class Factory : ModelLoaderFactory<FeedMedia, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<FeedMedia, InputStream> {
            return FeedMediaLoader(
                multiFactory.build(
                    GlideUrl::class.java,
                    InputStream::class.java
                )
            )
        }

        override fun teardown() {}
    }
}
